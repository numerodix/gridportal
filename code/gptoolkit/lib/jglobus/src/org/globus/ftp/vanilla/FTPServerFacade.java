/*
 * Portions of this file Copyright 1999-2005 University of Chicago
 * Portions of this file Copyright 1999-2005 The University of Southern California.
 *
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html.
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */
package org.globus.ftp.vanilla;

import org.globus.net.SocketFactory;
import org.globus.util.Util;
import org.globus.net.ServerSocketFactory;
import org.globus.ftp.exception.FTPException;
import org.globus.ftp.exception.ServerException;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.FTPReplyParseException;
import org.globus.ftp.Session;
import org.globus.ftp.HostPort;
import org.globus.ftp.HostPort6;
import org.globus.ftp.DataSource;
import org.globus.ftp.Options;
import org.globus.ftp.DataSink;
import org.globus.ftp.dc.DataChannelFactory;
import org.globus.ftp.dc.TaskThread;
import org.globus.ftp.dc.Task;
import org.globus.ftp.dc.ActiveConnectTask;
import org.globus.ftp.dc.TransferContext;
import org.globus.ftp.dc.PassiveConnectTask;
import org.globus.ftp.dc.SimpleDataChannelFactory;
import org.globus.ftp.dc.SimpleTransferContext;
import org.globus.ftp.dc.LocalReply;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.net.Socket;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
   <b>
   This class is not ment directly for the users.
   </b>
   This class represents the part of the client responsible for data 
   channel management. Especially when the remote server is in the passive
   mode, it behaves a lot like a local server. Thus its interface looks
   very much like a server interface.
   <br>
   Current implementation is multithreaded. One thread is used for thread
   management and one for each transfer (this makes sense in GridFTP 
   parallelism). 
   <br>
   The public methods can generally be divided into setter methods and active
   methods. Active methods are setActive(), setPassive(), retrieve(),
   and store(), and setter methods are the remaining.
   Setter methods do not generally throw exceptions related to ftp.
   Settings are not checked for correctness until the server 
   is asked to performed some action, which is done by active methods.
   So you are safe to cal setXX() methods with any argument you like, until
   you call one of the "active" methods mentioned above.
   <br>
   The managing thread is not started until one of the "active" methods is
   called: setActive(),  retrieve(), or store(). These methods
   are asynchronous (return before completion) and the action is undertaken
   by the local manager thread. From this point on, all communication
   back to the caller is done through unidirectional local control
   channel. Information is communicated back to the user in form of FTP
   replies (instances of LocalReply). Generally, the sequence of
   replies should be the same as when communicating with remote server
   during the transfer (1xx intermediary reply; markers; final 226).
   Exceptions are serialized into 451 negative reply.
 **/
public class FTPServerFacade {

    private static Log logger = 
        LogFactory.getLog(FTPServerFacade.class.getName());

    /**
       local server socket parameter; used in setPassive()
     **/
    public static final int ANY_PORT = 0;
    /**
       local server socket parameter; used in setPassive()
     **/
    public static final int DEFAULT_QUEUE = 100;

    protected Session session;
    protected LocalControlChannel localControlChannel;
    protected DataChannelFactory dataChannelFactory;
    protected ServerSocket serverSocket;
    protected FTPControlChannel remoteControlChannel;
    protected HostPort remoteServerAddress;

    // used only by FTPServerFacade
    private TaskThread taskThread;

    /**
       Data channels are operated in multithreaded manner and they pass
       information (including exceptions) to the user using the local
       control channel. In the unlikely event that it fails, there is no
       way to communicate the exception to the user. In such circumstances
       this method should be called to print the exception directly to console.
     **/
    public static void cannotPropagateError(Throwable e) {
        logger.error("Exception occured in the exception handling " +
                     "code, so it cannot be properly propagated to " +
                     "the user", e);
    }

    public FTPServerFacade(FTPControlChannel remoteControlChannel) {
        this.remoteControlChannel = remoteControlChannel;
        this.session = new Session();
        this.localControlChannel = new LocalControlChannel();
        this.dataChannelFactory = new SimpleDataChannelFactory();
    }

    /**
       Use this method to get the client end of the local 
       control channel. It is the only way to get the
       information of the current transfer state.
     **/
    public BasicClientControlChannel getControlChannel() {
        return localControlChannel;
    }

    /**
       @return the session object associated with this server
     **/
    public Session getSession() {
        return session;
    }

    // unconditional authorization
    /**
       No need for parameters; locally you are always authorized.
     **/
    public void authorize() {
        session.authorized = true;
    }

    public void setTransferType(int type) {
        session.transferType = type;
    }

    public void setTransferMode(int mode) {
        session.transferMode = mode;
    }

    public void setProtectionBufferSize(int size) {
        session.protectionBufferSize = size;
    }

    /**
       Do nothing; this class does not support any options
     **/
    public void setOptions(Options opts) {
    }

    /**
       Behave like setPassive(ANY_PORT, DEFAULT_QUEUE)
     **/
    public HostPort setPassive() throws IOException{
        return setPassive(ANY_PORT, DEFAULT_QUEUE);
    }

    /**
       Start the local server
       @param port required server port; can be set to ANY_PORT
       @param queue max size of queue of awaiting new connection
       requests
       @return the server address
     **/
    public HostPort setPassive(int port, int queue) 
        throws IOException{

        if (serverSocket == null) { 
            ServerSocketFactory factory = 
                ServerSocketFactory.getDefault();
            serverSocket = factory.createServerSocket(port, queue);
        }
        
        session.serverMode = Session.SERVER_PASSIVE;
        
        String address = Util.getLocalHostAddress();
        int localPort = serverSocket.getLocalPort();

        if (remoteControlChannel.isIPv6()) {
            String version = HostPort6.getIPAddressVersion(address);
            session.serverAddress = 
                new HostPort6(version, address, localPort);
        } else {
            session.serverAddress = 
                new HostPort(address, localPort);
        }

        logger.debug("started passive server at port " + 
                     session.serverAddress.getPort());
        return session.serverAddress;
    
    }

    /**
        Asynchronous; return before completion.
        Connect to the remote server.
        Any exception that would occure will not be thrown but
        returned through the local control channel.
     **/
    public void setActive(HostPort hp)
        throws UnknownHostException,
               ClientException,
               IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("hostport: " + hp.getHost() + " " + hp.getPort());
        }
        session.serverMode = Session.SERVER_ACTIVE;
        this.remoteServerAddress = hp;
    }

    /**
       Convert the exception to a negative 451 reply, and pipe
       it to the control channel.
     **/
    protected void exceptionToControlChannel(Throwable e,
                                             String msg) {
        // this could be reimplemented.
        // Now the exception is serialized to the control channel.
        // but it could be simply appended to the LocalReply,
        // if LocalReply had such functionality.
        exceptionToControlChannel(e, msg, localControlChannel);
    }

    /**
       Convert the exception to a negative 451 reply, and pipe
       it to the provided control channel.
     **/
    public static void exceptionToControlChannel(
                                Throwable e, 
                                String msg,
                                BasicServerControlChannel control) {
        // how to convert exception stack trace to string?
        // i am sure it can be done easier. 
        java.io.StringWriter writer = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(writer));
        String stack = writer.toString();

        // 451 Requested action aborted: local error in processing.
        LocalReply reply = new LocalReply(451, msg + "\n" + 
                                          e.toString() + "\n" +
                                          stack);
        control.write(reply);

    }

    /**
       Asynchronous; return before completion.
       Start the incoming transfer and 
       store the file to the supplied data sink.
       Any exception that would occure will not be thrown but
       returned through the local control channel.
     **/
    public void store(DataSink sink) {
        try {
            localControlChannel.resetReplyCount();
            TransferContext context= createTransferContext();

            if (session.serverMode == session.SERVER_PASSIVE) {
                runTask(createPassiveConnectTask(sink, context));
            } else {
                runTask(createActiveConnectTask(sink, context));
            }
        } catch (Exception e) {
            exceptionToControlChannel(e, "ocurred during store()");
        }
    }

    /**
       Asynchronous; return before completion.
       Start the outgoing transfer 
       reading the data from the supplied data source.
       Any exception that would occure will not be thrown but
       returned through the local control channel.
     **/
    public void retrieve(DataSource source) {
        try {
            localControlChannel.resetReplyCount();
            TransferContext context= createTransferContext();

            if (session.serverMode == session.SERVER_PASSIVE) {
                runTask(createPassiveConnectTask(source, context));
            } else {
                runTask(createActiveConnectTask(source, context));
            }
        } catch (Exception e) {
            exceptionToControlChannel(e, "ocurred during retrieve()");
        }
    }

    /**
       close data channels, but not control, nor the server 
    **/
    public void abort() throws IOException{
    }
    
    protected void transferAbort() {
        if (session.serverMode == session.SERVER_PASSIVE) {
            unblockServer();
	    stopTaskThread();
	}
    }

    protected void unblockServer() {
        if (serverSocket == null) {
            return;
        }
        String address = Util.getLocalHostAddress();
        int port = serverSocket.getLocalPort();
        // this is a hack to ensue the server socket is 
        // unblocked from accpet()
        // but this is not guaranteed to work still
        SocketFactory factory = SocketFactory.getDefault();
        Socket s = null;
        try {
            s = factory.createSocket(address, port);
            s.getInputStream();
        } catch (Exception e) {
        } finally {
            if (s != null) {
                try { s.close(); } catch (Exception e) {}
            }
        }
    }

    public void close() 
        throws IOException {
        logger.debug("close data channels");
        abort();

        logger.debug("close server socket");
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
            unblockServer();
        }
        
        stopTaskThread();
    }
    
    /**
       Use this as an interface to the local manager thread.
       This submits the task to the thread queue.
       The thread will perform it when it's ready with other
       waiting tasks.
     **/
    private void runTask(Task task) {
        if (taskThread == null) {
            taskThread = new TaskThread();
        }
        taskThread.runTask(task);
    }

    protected void stopTaskThread() {
        logger.debug("stop master thread");
        if (taskThread != null) {
            taskThread.stop();
            taskThread.join();
            taskThread = null;
        }
    }

    // task "factories":
    // use these methods to create tasks

    private PassiveConnectTask createPassiveConnectTask(DataSource source,
                                                        TransferContext context) {
        return new PassiveConnectTask(serverSocket, 
                                      source, 
                                      localControlChannel, 
                                      session, 
                                      dataChannelFactory,
                                      context);
    }

    private PassiveConnectTask createPassiveConnectTask(DataSink sink,
                                                        TransferContext context) {
        return new PassiveConnectTask(serverSocket, 
                                      sink, 
                                      localControlChannel, 
                                      session,
                                      dataChannelFactory,
                                      context);
    }
    
    private ActiveConnectTask createActiveConnectTask(DataSource source,
                                                      TransferContext context) {
        return new ActiveConnectTask(this.remoteServerAddress,
                                     source, 
                                     localControlChannel,
                                     session,
                                     dataChannelFactory,
                                     context);
    }

    private ActiveConnectTask createActiveConnectTask(DataSink sink, 
                                                      TransferContext context) {
        return new ActiveConnectTask(this.remoteServerAddress,
                                     sink, 
                                     localControlChannel,
                                     session,
                                     dataChannelFactory,
                                     context);
    }

    // inner classes 

    /**
      This inner class represents a local control channel.
      One process can write replies using BasicServerControlChannel
      interface, and the other can read replies using
      BasicClientControlChannel interface.
    **/
    protected class LocalControlChannel 
        extends BasicClientControlChannel 
        implements BasicServerControlChannel{

        // FIFO queue of Replies
        private LinkedList replies = null;
        // how many replies have been pushed so far
        private int replyCount = 0;

        public LocalControlChannel() {
            replies = new LinkedList();
        }

        protected synchronized void push(Reply newReply) {
            logger.debug("push reply:" + newReply.toString());
            replies.add(newReply);
            replyCount++;
            notify();
        }

        // blocking pop from queue
        protected synchronized Reply pop() throws InterruptedException {
            logger.debug("pop reply");
            try {
                while (replies.isEmpty()) {
                    wait();
                }
            } catch (InterruptedException e) {
                throw e;
            }
            return (Reply)replies.removeFirst();
        }

        //non blocking; check if queue is ready for pop
        public synchronized boolean ready() {
            return (!replies.isEmpty());
        }
        
        public synchronized int getReplyCount() {
            return replyCount;
        }

        public synchronized void resetReplyCount() {
            replies.clear();
            replyCount = 0;
        }

        public Reply read() 
            throws IOException,
                   FTPReplyParseException, 
                   ServerException{
            try {
                return pop();
            } catch (InterruptedException e) {
                ServerException se = new ServerException(FTPException.UNSPECIFIED,
                                                         "interrupted while waiting.");
                se.setRootCause(e);
                throw se;
            }
        }

        public void write(Reply reply) {
            logger.debug("writing reply");
            push(reply);
            logger.debug("wrote reply");
        }
        
        public void waitFor(Flag aborted,
                             int ioDelay,
                             int maxWait)       
            throws ServerException,
                   IOException,
                   InterruptedException{
            int i = 0;
            logger.debug("waiting for reply in local control channel");
            while( !ready()) {
                if (aborted.flag) throw new InterruptedException();
                logger.debug("slept " + i);
                Thread.sleep(ioDelay);
                i += ioDelay;
                if (maxWait != WAIT_FOREVER 
                    && i >= maxWait) {
                    logger.debug("timeout");
                    throw new ServerException(ServerException.REPLY_TIMEOUT);
                }
            }
            logger.debug("local control channel ready");
        }

        public void abortTransfer() {
            transferAbort();
        }

    }// class localControlChannel

    protected TransferContext createTransferContext() {
        return SimpleTransferContext.getDefault();
    }

} // FTPServerFacade
        




