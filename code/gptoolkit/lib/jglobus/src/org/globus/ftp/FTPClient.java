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
package org.globus.ftp;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.File;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.globus.ftp.exception.FTPReplyParseException;
import org.globus.ftp.exception.UnexpectedReplyCodeException;
import org.globus.ftp.exception.FTPException;
import org.globus.ftp.vanilla.FTPControlChannel;
import org.globus.ftp.vanilla.FTPServerFacade;
import org.globus.ftp.vanilla.BasicClientControlChannel;
import org.globus.ftp.vanilla.Command;
import org.globus.ftp.vanilla.Reply;
import org.globus.ftp.vanilla.TransferMonitor;
import org.globus.ftp.vanilla.TransferState;

/**
 * This is the main user interface for FTP operations.
 * Use this class for client - server or third party transfers
 * that do not require GridFTP extensions.
 * Consult the manual for general usage.
 * <br><b>Note:</b> If using with GridFTP servers operations like
 * {@link #setMode(int) setMode()}, {@link #setType(int) setType()} that
 * affect data channel settings <b>must</b> be called before passive
 * or active data channel mode is set. 
 **/
public class FTPClient {

    private static Log logger = LogFactory.getLog(FTPClient.class.getName());

    // represents the state of interaction with remote server
    protected Session session;
    protected FTPControlChannel controlChannel;
    
    // the local server handles data channels
    protected FTPServerFacade localServer;
    
    /* needed for last modified command */
    protected SimpleDateFormat dateFormat = null;

    /* for subclasses */
    protected FTPClient() {
    }
    
    /**
     * Constructs client and connects it to the remote server.
     * @param host remote server host
     * @param port remote server port
     */
    public FTPClient(String host, int port)
        throws IOException, ServerException {
        session = new Session();
        
        controlChannel = new FTPControlChannel(host, port);
        controlChannel.open();
        
        localServer = new FTPServerFacade(controlChannel);
        localServer.authorize();
    }
    
    /**
     * Returns the remote file size.
     *
     * @param     filename filename get the size for.
     * @return    size of the file.
     * @exception FTPException if the file does not exist or 
     *            an error occured.
     */
    public long getSize(String filename) 
        throws IOException, ServerException {
        if (filename == null) {
            throw new IllegalArgumentException("Required argument missing");
        }
        Command cmd = new Command("SIZE", filename);
        Reply reply = null;
        try {
            reply = controlChannel.execute(cmd);
            return Long.parseLong(reply.getMessage());
        } catch (NumberFormatException e) {
            throw ServerException.embedFTPReplyParseException(
                         new FTPReplyParseException(
                                FTPReplyParseException.MESSAGE_UNPARSABLE,
                               "Could not parse size: " + reply.getMessage()));
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(urce);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        }
    }

    /**
     * Returns last modification time of the specifed file.
     *
     * @param     filename filename get the last modification time for.
     * @return    the time and date of the last modification.
     * @exception FTPException if the file does not exist or
     *            an error occured.
     */
    public Date getLastModified(String filename)
        throws IOException, ServerException {
        if (filename == null) {
            throw new IllegalArgumentException("Required argument missing");
        }
        Command cmd = new Command("MDTM", filename);
        Reply reply = null;
        try {
            reply = controlChannel.execute(cmd);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused changing transfer mode");
        }

        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        try {
            return dateFormat.parse(reply.getMessage());
        } catch (ParseException e) {
            throw ServerException.embedFTPReplyParseException(
                     new FTPReplyParseException(
                            0,
                            "Invalid file modification time reply: " + reply));
        }
    }

    /**
     * Checks if given file/directory exists on the server.
     *
     * @param  filename 
     *         file or directory name
     * @return true if the file exists, false otherwise.
     */
    public boolean exists(String filename)
        throws IOException, ServerException {
        if (filename == null) {
            throw new IllegalArgumentException("Required argument missing");
        }
        try {
            Reply reply =
                controlChannel.exchange(new Command("RNFR", filename));
            if (Reply.isPositiveIntermediate(reply)) {
                controlChannel.execute(new Command("ABOR"));
                return true;
            } else {
                return false;
            }
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Abort failed");
        }
    }

    /**
     * Changes the remote current working directory.
     */
    public void changeDir(String dir) 
        throws IOException, ServerException {
        if (dir == null) {
            throw new IllegalArgumentException("Required argument missing");
        }
        Command cmd = new Command("CWD", dir);
        try {
            controlChannel.execute(cmd);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused changing directory");
        }
    }

    /**
     * Deletes the remote directory.
     */
    public void deleteDir(String dir) 
        throws IOException, ServerException {
        if (dir == null) {
            throw new IllegalArgumentException("Required argument missing");
        }
        Command cmd = new Command("RMD", dir);
        try {
            controlChannel.execute(cmd);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused deleting directory");
        }
    }

    /**
     * Deletes the remote file.
     */
    public void deleteFile(String filename)
        throws IOException, ServerException {
        if (filename == null) {
            throw new IllegalArgumentException("Required argument missing");
        }
        Command cmd = new Command("DELE", filename);
        try {
            controlChannel.execute(cmd);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused deleting file");
        }
    }

    /**
     * Creates remote directory.
     */
    public void makeDir(String dir) 
        throws IOException, ServerException {
        if (dir == null) {
            throw new IllegalArgumentException("Required argument missing");
        }
        Command cmd = new Command("MKD", dir);
        try {
            controlChannel.execute(cmd);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused creating directory");
        }
    }

    /**
     * Renames remote directory.
     */
    public void rename(String oldName, String newName)
        throws IOException, ServerException {
        if (oldName == null || newName == null) {
            throw new IllegalArgumentException("Required argument missing");
        }
        Command cmd = new Command("RNFR", oldName);
        try {
            Reply reply = controlChannel.exchange(cmd);
            if (!Reply.isPositiveIntermediate(reply)) {
                throw new UnexpectedReplyCodeException(reply);
            }
            controlChannel.execute(new Command("RNTO", newName));
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused renaming file");
        }
    }

    /**
     * Returns remote current working directory.
     * @return remote current working directory.
     */
    public String getCurrentDir() throws IOException, ServerException {
        Reply reply = null;
        try {
            reply = controlChannel.execute(Command.PWD);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused returning current directory");
        }
        String strReply = reply.getMessage();
        if (strReply.length() > 0 && strReply.charAt(0) == '"') {
            return strReply.substring(1, strReply.indexOf('"', 1));
        } else {
            throw ServerException.embedFTPReplyParseException(
                                new FTPReplyParseException(
                                        0,
                                        "Cannot parse 'PWD' reply: " + reply));
        }
    }

    /**
     * Changes remote current working directory to the higher level.
     */
    public void goUpDir() throws IOException, ServerException {
        try {
            controlChannel.execute(Command.CDUP);
            // alternative: changeDir("..");
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused changing current directory");
        }
    }

    private class ByteArrayDataSink implements DataSink {

        private ByteArrayOutputStream received;

        public ByteArrayDataSink() {
            this.received = new ByteArrayOutputStream(1000);
        }
        
        public void write(Buffer buffer) throws IOException {
            if (logger.isDebugEnabled()) {
                logger.debug(
                             "received "
                             + buffer.getLength()
                             + " bytes of directory listing");
            }
            this.received.write(buffer.getBuffer(), 0, buffer.getLength());
        }

        public void close() throws IOException {
        }
        
        public ByteArrayOutputStream getData() {
            return this.received;
        }
    }

    /**
     * Performs remote directory listing. Sends 'LIST -d *' command.
     *
     * <br><b>Note</b>:<i> 
     *       This function can only parse Unix ls -d like output. Please
     *       note that the LIST output is unspecified in the FTP standard and 
     *       each server might return slightly different output causing the 
     *       parsing to fail.
     *       Also, if the ftp server does not accept -d option or support 
     *       wildcards, this method might fail. For example, this command will
     *       fail on GridFTP server distributed with GT 4.0.0. 
     *       It is strongly recommended to use {@link #mlsd() mlsd()}
     *       function instead.</i>
     *
     * @return Vector list of {@link FileInfo FileInfo} objects, representing
     *         remote files
     * @see #mlsd()
     */
    public Vector list() throws ServerException, ClientException, IOException {
        return list("*");
    }

    /**
     * Performs remote directory listing with the specified filter. 
     * Sends 'LIST -d &lt;filter&gt;' command.
     *
     * <br><b>Note</b>: <i>
     *       This function can only parse Unix ls -d like output. Please
     *       note that the LIST output is unspecified in the FTP standard and 
     *       each server might return slightly different output causing the 
     *       parsing to fail.
     *       Also, if the ftp server does not accept -d option or support 
     *       wildcards, this method might fail. For example, this command will
     *       fail on GridFTP server distributed with GT 4.0.0. 
     *       It is strongly recommended to use {@link #mlsd(String) mlsd()}
     *       function instead. </i>
     *
     * @param filter "*" for example, can be null.
     * @return Vector list of {@link FileInfo FileInfo} objects, representing
     *         remote files
     * @see #mlsd(String)
     */
    public Vector list(String filter)
        throws ServerException, ClientException, IOException {
        return list(filter, "-d");
    }
    
    /**
     * Performs remote directory listing with the specified filter and 
     * modifier. Sends 'LIST &lt;modifier&gt; &lt;filter&gt;' command. 
     *
     * <br><b>Note</b>: <i>
     *       This function can only parse Unix ls -d like output. Please
     *       note that the LIST output is unspecified in the FTP standard and 
     *       each server might return slightly different output causing the 
     *       parsing to fail.
     *       Also, please keep in mind that the ftp server might not
     *       recognize or support all the different modifiers or filters.
     *       In fact, some servers such as GridFTP server distributed with
     *       GT 4.0.0 does not support any modifiers or filters
     *       (strict RFC 959 compliance).
     *       It is strongly recommended to use {@link #mlsd(String) mlsd()}
     *       function instead.</i>
     *
     * @param filter "*" for example, can be null.
     * @param modifier "-d" for example, can be null.
     * @return Vector list of {@link FileInfo FileInfo} objects, representing
     *         remote files
     * @see #mlsd(String)
     */
    public Vector list(String filter, String modifier)
        throws ServerException, ClientException, IOException {

        ByteArrayDataSink sink = new ByteArrayDataSink();
        
        list(filter, modifier, sink);
        
        ByteArrayOutputStream received = sink.getData();

        // transfer done. Data is in received stream.
        // convert it to a vector.
        
        BufferedReader reader =
            new BufferedReader(new StringReader(received.toString()));
        
        Vector fileList = new Vector();
        FileInfo fileInfo = null;
        String line = null;
        
        while ((line = reader.readLine()) != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("line ->" + line);
            }
            if (line.startsWith("total"))
                continue;
            try {
                fileInfo = new FileInfo(line);
            } catch (FTPException e) {
                ClientException ce =
                    new ClientException(
                                        ClientException.UNSPECIFIED,
                                        "Could not create FileInfo");
                ce.setRootCause(e);
                throw ce;
            }
            fileList.addElement(fileInfo);
        }
        return fileList;
    }

    /**
     * Performs directory listing and writes the result 
     * to the supplied data sink.
     * This method is allowed in ASCII mode only.
     *
     * <br><b>Note</b>: <i>
     *       Please keep in mind that the ftp server might not
     *       recognize or support all the different modifiers or filters.
     *       In fact, some servers such as GridFTP server distributed with
     *       GT 4.0.0 does not support any modifiers or filters
     *       (strict RFC 959 compliance).
     *       It is strongly recommended to use {@link #mlsd(String,DataSink)
     *       mlsd()} function instead.</i>
     * 
     * @param filter remote list command file filter, eg. "*"
     * @param modifier remote list command modifier, eg. "-d"
     * @param sink data destination
     **/
    public void list(String filter, String modifier, DataSink sink)
        throws ServerException, ClientException, IOException {
        String arg = null;

        if (modifier != null) {
            arg = modifier;
        } 
        if (filter != null) {
            arg = (arg == null) ? filter : arg + " " + filter;
        }
        
        Command cmd = new Command("LIST", arg);

        performTransfer(cmd, sink);
    }

    /**
     * Performs remote directory listing of the current directory.
     * Sends 'NLST' command. 
     *
     * @return Vector list of {@link FileInfo FileInfo} objects, representing
     *         remote files
     */
    public Vector nlist()
        throws ServerException, ClientException, IOException {
        return nlist(null);
    }

    /**
     * Performs remote directory listing on the given path.
     * Sends 'NLST &lt;path&gt;' command. 
     *
     * @param path directory to perform listing of. If null, listing
     *        of current directory will be performed.
     * @return Vector list of {@link FileInfo FileInfo} objects, representing
     *         remote files
     */
    public Vector nlist(String path) 
        throws ServerException, ClientException, IOException {

        ByteArrayDataSink sink = new ByteArrayDataSink();
        
        nlist(path, sink);
        
        ByteArrayOutputStream received = sink.getData();

        // transfer done. Data is in received stream.
        // convert it to a vector.
        
        BufferedReader reader =
            new BufferedReader(new StringReader(received.toString()));
        
        Vector fileList = new Vector();
        FileInfo fileInfo = null;
        String line = null;
        
        while ((line = reader.readLine()) != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("line ->" + line);
            }

            fileInfo = new FileInfo();
            fileInfo.setName(line);
            fileInfo.setFileType(FileInfo.UNKNOWN_TYPE);

            fileList.addElement(fileInfo);
        }

        return fileList;
    }

    /**
     * Performs remote directory listing on the given path.
     * Sends 'NLST &lt;path&gt;' command. 
     *
     * @param path directory to perform listing of. If null, listing
     *        of current directory will be performed.
     * @param sink sink to which the listing data will be written.
     */
    public void nlist(String path, DataSink sink) 
        throws ServerException, ClientException, IOException {
        Command cmd = (path == null) ?
            new Command("NLST") :
            new Command("NLST", path);
        
        performTransfer(cmd, sink);
    }

    /**
     * Get info of a certain remote file in Mlsx format.
     */
    public MlsxEntry mlst(String fileName)
        throws IOException, ServerException {
        try {
            Reply reply = controlChannel.execute(new Command("MLST", fileName));
            String replyMessage = reply.getMessage();
            StringTokenizer replyLines =
                new StringTokenizer(
                                    replyMessage,
                                    System.getProperty("line.separator"));
            if (replyLines.hasMoreElements()) {
                replyLines.nextElement();
            } else {
                throw new FTPException(FTPException.UNSPECIFIED,
                                       "Expected multiline reply");
            }
            if (replyLines.hasMoreElements()) {
                String line = (String) replyLines.nextElement();
                return new MlsxEntry(line);
            } else {
                throw new FTPException(FTPException.UNSPECIFIED,
                                       "Expected multiline reply");
            }
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                            urce,
                            "Server refused MLST command");
        } catch (FTPException e) {
            ServerException ce =
                new ServerException(
                                    ClientException.UNSPECIFIED,
                                    "Could not create MlsxEntry");
            ce.setRootCause(e);
            throw ce;
        }
    }

    /**
     * Performs remote directory listing of the current directory.
     * Sends 'MLSD' command. 
     *
     * @return Vector list of {@link MlsxEntry MlsxEntry} objects, representing
     *         remote files
     */
    public Vector mlsd() 
        throws ServerException, ClientException, IOException {
        return mlsd(null);
    }

    /**
     * Performs remote directory listing on the given path.
     * Sends 'MLSD &lt;path&gt;' command. 
     *
     * @param path directory to perform listing of. If null, listing
     *        of current directory will be performed.
     * @return Vector list of {@link MlsxEntry MlsxEntry} objects, representing
     *         remote files
     */
    public Vector mlsd(String path)
        throws ServerException, ClientException, IOException {

        ByteArrayDataSink sink = new ByteArrayDataSink();

        mlsd(path, sink);
        
        ByteArrayOutputStream received = sink.getData();

        // transfer done. Data is in received stream.
        // convert it to a vector.

        BufferedReader reader =
            new BufferedReader(new StringReader(received.toString()));

        Vector fileList = new Vector();
        MlsxEntry entry = null;
        String line = null;

        while ((line = reader.readLine()) != null) {

            if (logger.isDebugEnabled()) {
                logger.debug("line ->" + line);
            }

            try {
                entry = new MlsxEntry(line);
            } catch (FTPException e) {
                ClientException ce =
                    new ClientException(
                                        ClientException.UNSPECIFIED,
                                        "Could not create MlsxEntry");
                ce.setRootCause(e);
                throw ce;
            }

            fileList.addElement(entry);
            
        }
        return fileList;
    }

    /**
     * Performs remote directory listing on the given path.
     * Sends 'MLSD &lt;path&gt;' command. 
     *
     * @param path directory to perform listing of. If null, listing
     *        of current directory will be performed.
     * @param sink sink to which the listing data will be written.
     */
    public void mlsd(String path, DataSink sink)
        throws ServerException, ClientException, IOException {
        Command cmd = (path == null) ?
            new Command("MLSD") :
            new Command("MLSD", path);
        
        performTransfer(cmd, sink);
    }

    /**
     * check performed at the beginning of list()
     **/
    protected void listCheck() throws ClientException {
        if (session.transferType != Session.TYPE_ASCII) {
            throw new ClientException(
                                ClientException.BAD_MODE,
                                "list requires ASCII type");
        }
    }

    protected void checkTransferParamsGet()
        throws ServerException, IOException, ClientException {
        checkTransferParams();
    }
    
    protected void checkTransferParamsPut()
        throws ServerException, IOException, ClientException {
        checkTransferParams();
    }
    
    protected void checkTransferParams()
        throws ServerException, IOException, ClientException {
        Session localSession = localServer.getSession();
        session.matches(localSession);
        logger.debug("sessions match");
        
        // if transfer modes have not been defined, 
        // set this (dest) as active
        if (session.serverMode == session.SERVER_DEFAULT) {
            logger.debug("defining remote pasv, local active");
            // resulting HostPort stored in session
            setPassive();
            // HostPort read from session
            setLocalActive();
        }
    }

    protected void performTransfer(Command cmd, DataSink sink)
        throws ServerException, ClientException, IOException {
        listCheck();
        checkTransferParamsGet();
        
        controlChannel.write(cmd);
        
        localServer.store(sink);
        transferRun(localServer.getControlChannel(), null);
    }

    /** Sets transfer type.
     * @param type should be {@link Session#TYPE_IMAGE TYPE_IMAGE},
     *                       {@link Session#TYPE_ASCII TYPE_ASCII},
     *                       {@link Session#TYPE_LOCAL TYPE_LOCAL},
     *                       {@link Session#TYPE_EBCDIC TYPE_EBCDIC}
     **/
    public void setType(int type) throws IOException, ServerException {

        localServer.setTransferType(type);

        String typeStr = null;
        switch (type) {
        case Session.TYPE_IMAGE :
            typeStr = "I";
            break;
        case Session.TYPE_ASCII :
            typeStr = "A";
            break;
        case Session.TYPE_LOCAL :
            typeStr = "E";
            break;
        case Session.TYPE_EBCDIC :
            typeStr = "L";
            break;
        default :
            throw new IllegalArgumentException("Bad type: " + type);
        }
        
        Command cmd = new Command("TYPE", typeStr);
        try {
            controlChannel.execute(cmd);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused changing transfer mode");
        }
        
        this.session.transferType = type;
    }

    /**
     * Sets transfer mode.
     * @param mode should be {@link Session#MODE_STREAM MODE_STREAM}, 
     *                       {@link Session#MODE_BLOCK MODE_BLOCK}
     **/
    public void setMode(int mode) throws IOException, ServerException {
        
        String modeStr = null;
        switch (mode) {
        case Session.MODE_STREAM :
            modeStr = "S";
            break;
        case Session.MODE_BLOCK :
            modeStr = "B";
            break;
        default :
            throw new IllegalArgumentException("Bad mode: " + mode);
        }
        
        actualSetMode(mode, modeStr);
    }

    protected void actualSetMode(int mode, String modeStr)
        throws IOException, ServerException {
        
        localServer.setTransferMode(mode);
        Command cmd = new Command("MODE", modeStr);
        try {
            controlChannel.execute(cmd);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused changing transfer mode");
        }
        
        this.session.transferMode = mode;
    }

    /** Sets protection buffer size (defined in RFC 2228)
     * @param size the size of buffer
     */
    public void setProtectionBufferSize(int size)
        throws IOException, ServerException {
        
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        
        localServer.setProtectionBufferSize(size);
        try {
            Command cmd = new Command("PBSZ", Integer.toString(size));
            controlChannel.execute(cmd);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused setting protection buffer size");
        }
        this.session.protectionBufferSize = size;
    }

    /**
       Aborts the current transfer. FTPClient is not thread
       safe so be careful with using this procedure, which will
       typically happen in multi threaded environment.
       Especially during client-server two party transfer,
       calling abort() may result with exceptions being thrown in the thread
       that currently perform the transfer.
    **/
    public void abort() throws IOException, ServerException {
        // TODO: This might need to be reimplemented to support
        // sending out of bounds urgent TCP messages

        try {
            controlChannel.execute(Command.ABOR);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused changing transfer mode");
        } finally {
            localServer.abort();
        }
    }
    
    /** Closes connection. Sends QUIT command and closes connection 
     *  even if the server reply was not positive. Also, closes
     *  the local server. This function will block until the server
     *  sends a reply to the QUIT command.
     **/
    public void close() 
        throws IOException, ServerException {
        close(false);
    }

    /** Closes connection. Sends QUIT and closes connection 
     *  even if the server reply was not positive. Also, closes
     *  the local server.
     *
     * @param ignoreQuitReply if true the <code>QUIT</code> command
     *        will be sent but the client will not wait for the
     *        server's reply. If false, the client will block
     *        for the server's reply.
     **/
    public void close(boolean ignoreQuitReply) 
        throws IOException, ServerException {
        try {
            if (ignoreQuitReply) {
                controlChannel.write(Command.QUIT);
            } else {
                controlChannel.execute(Command.QUIT);
            }
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused closing");
        } finally {
            try {
                controlChannel.close();
            } finally {
                localServer.close();
            }
        }
    }

    /**
     * Returns true if the given feature is supported by remote server, 
     * false otherwise.
     * 
     * @return true if the given feature is supported by remote server,
     *         false otherwise.
     */
    public boolean isFeatureSupported(String feature)
        throws IOException, ServerException {
        return getFeatureList().contains(feature);
    }
    
    /**
     * Returns list of features supported by remote server.
     * @return list of features supported by remote server.
     */
    public FeatureList getFeatureList() throws IOException, ServerException {

        if (this.session.featureList != null) {
            return this.session.featureList;
        }

        // TODO: this can also be optimized. Instead of parsing the
        // reply after it is reveiced, we can parse it as it is
        // received.
        Reply featReply = null;
        try {
            featReply = controlChannel.execute(Command.FEAT);

            if (featReply.getCode() != 211) {
                throw ServerException.embedUnexpectedReplyCodeException(
                                  new UnexpectedReplyCodeException(featReply),
                                  "Server refused returning features");
            }
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused returning features");
        }

        this.session.featureList = new FeatureList(featReply.getMessage());
        
        return session.featureList;
    }

    /**
     * Sets remote server to passive server mode.
     * @return the address at which the server is listening.
     */
    public HostPort setPassive() throws IOException, ServerException {
        Reply reply = null;
        try {
            reply = controlChannel.execute(
                    (controlChannel.isIPv6()) ? Command.EPSV : Command.PASV);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(urce);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        }
        String pasvReplyMsg = null;
        
        pasvReplyMsg = reply.getMessage();
        
        int openBracket = pasvReplyMsg.indexOf("(");
        int closeBracket = pasvReplyMsg.indexOf(")", openBracket);
        String bracketContent =
            pasvReplyMsg.substring(openBracket + 1, closeBracket);

        this.session.serverMode = session.SERVER_PASSIVE;

        HostPort hp = null;
        if (controlChannel.isIPv6()) {
            hp = new HostPort6(bracketContent);
            // since host information might be null
            // fill it it
            if (hp.getHost() == null) {
                ((HostPort6)hp).setVersion(HostPort6.IPv6);
                ((HostPort6)hp).setHost(controlChannel.getHost());
            }
        } else {
            hp = new HostPort(bracketContent);
        }
        
        this.session.serverAddress = hp;
        return hp;
    }

    /**
     * Sets remote server active, telling it to connect to the given
     * address.
     * @param hostPort the address to which the server should connect
     */
    public void setActive(HostPort hostPort)
        throws IOException, ServerException {
        Command cmd = new Command((controlChannel.isIPv6()) ? "EPRT" : "PORT", 
                                  hostPort.toFtpCmdArgument());
        try {
            controlChannel.execute(cmd);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(urce);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        }

        this.session.serverMode = session.SERVER_ACTIVE;
    }

    /**
       Sets remote server active, telling it to connect to the client.
       setLocalPassive() must be called beforehand.
    **/
    public void setActive()
        throws IOException, ServerException, ClientException {
        Session local = localServer.getSession();
        if (local.serverAddress == null) {
            throw new ClientException(ClientException.CALL_PASSIVE_FIRST);
        }
        setActive(local.serverAddress);
    }

    /** Starts local server in active server mode.
     **/
    public void setLocalActive() throws ClientException, IOException {
        if (session.serverAddress == null) {
            throw new ClientException(ClientException.CALL_PASSIVE_FIRST);
        }
        try {
            localServer.setActive(session.serverAddress);
        } catch (java.net.UnknownHostException e) {
            throw new ClientException(ClientException.UNKNOWN_HOST);
        }
    }

    /**
       Starts local server in passive server mode, with default parameters.
       In other words, behaves like 
       setLocalPassive(FTPServerFacade.ANY_PORT, FTPServerFacade.DEFAULT_QUEUE)
    **/
    public HostPort setLocalPassive() throws IOException {
        return localServer.setPassive();
    }
    
    /**
       Starts the local server in passive server mode.
       @param port port at which local server should be listening; 
       can be set to FTPServerFacade.ANY_PORT
       @param queue max size of queue of awaiting new connection
       requests
       @return the server address
    **/
    public HostPort setLocalPassive(int port, int queue) throws IOException {
        return localServer.setPassive(port, queue);
    }
    
    /**
       Changes the default client timeout parameters.
       In the beginning of the transfer, the critical moment is the wait
       for the initial server reply. If it does not arrive after timeout, 
       client assumes that the transfer could not start for some reason and
       aborts the operation. Default timeout in miliseconds 
       is Session.DEFAULT_MAX_WAIT. During the waiting period, 
       client polls the control channel once a certain period, which is by
       default set to Session.DEFAULT_WAIT_DELAY. 
       <br>
       Use this method to change these parameters.
       @param maxWait timeout in miliseconds
       @param waitDelay polling period
    **/
    public void setClientWaitParams(int maxWait, int waitDelay) {
        if (maxWait <= 0 || waitDelay <= 0) {
            throw new IllegalArgumentException("Parameter is less than 0");
        }
        this.session.maxWait = maxWait;
        this.session.waitDelay = waitDelay;
    }

    /**
     * Sets the supplied options to the server.
     */
    public void setOptions(Options opts) throws IOException, ServerException {
        Command cmd = new Command("OPTS", opts.toFtpCmdArgument());

        try {
            controlChannel.execute(cmd);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                urce,
                                "Server refused setting options");
        }
        
        localServer.setOptions(opts);
    }

    /**
     * Sets restart parameter of the next transfer.
     *
     * @param     restartData marker to use
     * @exception FTPException if the file does not exist or 
     *            an error occured.
     */
    public void setRestartMarker(RestartData restartData)
        throws IOException, ServerException {
        Command cmd = new Command("REST", restartData.toFtpCmdArgument());
        Reply reply = null;
        try {
            reply = controlChannel.exchange(cmd);
        } catch (FTPReplyParseException e) {
            throw ServerException.embedFTPReplyParseException(e);
        }

        if (!Reply.isPositiveIntermediate(reply)) {
            throw ServerException.embedUnexpectedReplyCodeException(
                                new UnexpectedReplyCodeException(reply));
        }
    }

    /**
     * Performs user authorization with specified
     * user and password.
     *
     * @param user username
     * @param password user password
     * @exception ServerException on server refusal
     */
    public void authorize(String user, String password)
        throws IOException, ServerException {
        
        Reply userReply = null;
        try {
            userReply = controlChannel.exchange(new Command("USER", user));
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        }

        if (Reply.isPositiveIntermediate(userReply)) {
            Reply passReply = null;
            
            try {
                passReply =
                    controlChannel.exchange(new Command("PASS", password));
            } catch (FTPReplyParseException rpe) {
                throw ServerException.embedFTPReplyParseException(rpe);
            }

            if (!Reply.isPositiveCompletion(passReply)) {
                throw ServerException.embedUnexpectedReplyCodeException(
                                   new UnexpectedReplyCodeException(passReply),
                                   "Bad password.");
            }

            // i'm logged in

        } else if (Reply.isPositiveCompletion(userReply)) {
            
            // i'm logged in 
            
        } else {
            throw ServerException.embedUnexpectedReplyCodeException(
                                new UnexpectedReplyCodeException(userReply),
                                "Bad user.");
        }
        this.session.authorized = true;
    }

    /**
       Retrieves the file from the remote server.
       @param remoteFileName remote file name
       @param sink sink to which the data will be written
       @param mListener restart marker listener (currently not used)
    */
    public void get(String remoteFileName,
                    DataSink sink,
                    MarkerListener mListener)
        throws IOException, ClientException, ServerException {
        TransferState state = actualGet(remoteFileName, sink, mListener);
        transferWait(state);
    }
    
    /**
       Retrieves the file from the remote server.
       @param remoteFileName remote file name
       @param sink sink to which the data will be written
       @param mListener restart marker listener (currently not used)
    */
    public TransferState asynchGet(String remoteFileName,
                                   DataSink sink,
                                   MarkerListener mListener)
        throws IOException, ClientException, ServerException {
        return actualGet(remoteFileName, sink, mListener);
    }
    
    protected TransferState actualGet(String remoteFileName,
                                      DataSink sink,
                                      MarkerListener mListener)
        throws IOException, ClientException, ServerException {

        checkTransferParamsGet();
        
        logger.debug("localserver.store()");
        localServer.store(sink);
        
        controlChannel.write(new Command("RETR", remoteFileName));
        
        return transferStart(localServer.getControlChannel(), mListener);
    }
    
    /**
     * Stores file at the remote server.
     * @param remoteFileName remote file name
     * @param source data will be read from here
     * @param mListener restart marker listener (currently not used)
     */
    public void put(String remoteFileName,
                    DataSource source,
                    MarkerListener mListener)
        throws IOException, ServerException, ClientException {
        put(remoteFileName, source, mListener, false);
    }
    
    /**
     * Stores file at the remote server.
     * @param remoteFileName remote file name
     * @param source data will be read from here
     * @param mListener restart marker listener (currently not used)
     * @param append append to the end of file or overwrite
     */
    public void put(String remoteFileName,
                    DataSource source,
                    MarkerListener mListener,
                    boolean append)
        throws IOException, ServerException, ClientException {
        TransferState state =
            actualPut(remoteFileName, source, mListener, append);
        transferWait(state);
    }

    /**
     * Stores file at the remote server.
     * @param remoteFileName remote file name
     * @param source data will be read from here
     * @param mListener restart marker listener (currently not used)
     */
    public TransferState asynchPut(String remoteFileName,
                                   DataSource source,
                                   MarkerListener mListener)
        throws IOException, ServerException, ClientException {
        return actualPut(remoteFileName, source, mListener, false);
    }

    /**
     * Stores file at the remote server.
     * @param remoteFileName remote file name
     * @param source data will be read from here
     * @param mListener restart marker listener (currently not used)
     * @param append append to the end of file or overwrite
     */
    public TransferState asynchPut(String remoteFileName,
                                   DataSource source,
                                   MarkerListener mListener,
                                   boolean append)
        throws IOException, ServerException, ClientException {
        return actualPut(remoteFileName, source, mListener, append);
    }
    
    protected TransferState actualPut(String remoteFileName,
                                      DataSource source,
                                      MarkerListener mListener,
                                      boolean append)
        throws IOException, ServerException, ClientException {
        checkTransferParamsPut();
        
        localServer.retrieve(source);
        
        if (append) {
            controlChannel.write(new Command("APPE", remoteFileName));
        } else {
            controlChannel.write(new Command("STOR", remoteFileName));
        }
        
        return transferStart(localServer.getControlChannel(), mListener);
    }

    /*
      3rd party transfer code
    */
    
    /**
     * Performs third-party transfer between two servers.
     *
     * @param remoteSrcFile source filename
     * @param destination   another client connected to destination server
     * @param remoteDstFile destination filename
     * @param append        enables append mode; if true,
     *                      data will be appened to the remote file, otherwise
     *                      file will be overwritten.
     * @param mListener     marker listener.
     *                      Can be set to null.
     */
    public void transfer(String remoteSrcFile,
                         FTPClient destination,
                         String remoteDstFile,
                         boolean append,
                         MarkerListener mListener)
        throws IOException, ServerException, ClientException {
        
        session.matches(destination.session);
        
        // if transfer modes have not been defined, 
        // set this (source) as active
        if (session.serverMode == session.SERVER_DEFAULT) {
            
            HostPort hp = destination.setPassive();
            setActive(hp);
            
        }

        destination.controlChannel.write(
                    new Command((append) ? "APPE" : "STOR", remoteDstFile));

        controlChannel.write(new Command("RETR", remoteSrcFile));
        
        transferRun(destination.controlChannel, mListener);
    }

    /**
       Actual transfer management.
       Transfer is controlled by two new threads listening
       to the two servers.
    **/
    protected void transferRun(BasicClientControlChannel other,
                               MarkerListener mListener)
        throws IOException, ServerException, ClientException {
        logger.debug("transferRun()");
        TransferState transferState = transferBegin(other, mListener);
        transferWait(transferState);
    }

    protected TransferState transferBegin(BasicClientControlChannel other,
                                          MarkerListener mListener) {
        // this structure will contain up to date information
        // about the state of transfer at both sides
        TransferState transferState = new TransferState(mListener);
        
        // thread monitoring our server during transfer
        // (that is, the server associated with this FTPClient)
        TransferMonitor ourMonitor =
            new TransferMonitor(
                                controlChannel,
                                transferState,
                                mListener,
                                session.maxWait,
                                session.waitDelay,
                                TransferMonitor.LOCAL);
        
        // thread monitoring other server during transfer
        // (that is, the server associated with the other FTPClient)
        TransferMonitor otherMonitor =
            new TransferMonitor(
                                other,
                                transferState,
                                mListener,
                                session.maxWait,
                                session.waitDelay,
                                TransferMonitor.REMOTE);
        
        ourMonitor.setOther(otherMonitor);
        otherMonitor.setOther(ourMonitor);
        
        // start two threads controling the transfer
        ourMonitor.start();
        otherMonitor.start();
        
        return transferState;
    }

    protected TransferState transferStart(BasicClientControlChannel other,
                                          MarkerListener mListener)
        throws IOException, ServerException, ClientException {
        TransferState transferState = transferBegin(other, mListener);
        logger.debug("wait for both transfer to start");
        transferState.waitForStart();
        logger.debug("transfer started");
        return transferState;
    }

    protected void transferWait(TransferState transferState)
        throws IOException, ServerException, ClientException {
        logger.debug("wait for both monitor threads to finish");
        transferState.waitForEnd();
        logger.debug("transfer finished");
    }

    /*
      end 3rd party transfer code
    */
    
    /**
     * Executes arbitrary operation on the server.
     *
     * <br><b>Note</b>: <i>This is potentially dangerous operation. 
     * Depending on the command executed it might put the server in a
     * different state from the state the client is expecting.</i>
     *
     * @param command command to execute
     * @exception IOException in case of I/O error.
     * @exception ServerException if operation failed.
     * @return the Reply to the operation.
     */
    public Reply quote(String command) throws IOException, ServerException {
        Command cmd = new Command(command);
        return doCommand(cmd);
    }

    /**
     * Executes site-specific operation (using the SITE command).
     *
     * <br><b>Note</b>: <i>This is potentially dangerous operation. 
     * Depending on the command executed it might put the server in a
     * different state from the state the client is expecting.</i>
     *
     * @param args parameters for the SITE operation.
     * @exception IOException in case of I/O error
     * @exception ServerException if operation failed.
     * @return the Reply to the operation.
     */
    public Reply site(String args) throws IOException, ServerException {
        Command cmd = new Command("SITE", args);
        return doCommand(cmd);
    }

    private Reply doCommand(Command cmd) throws IOException, ServerException {
        try {
            return controlChannel.execute(cmd);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(urce);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        }
    }

    /**
     * Reserve sufficient storage to accommodate the new file to be
     * transferred.
     *
     * @param     size the amount of space to reserve
     * @exception FTPException if an error occured.
     */
    public void allocate(long size) throws IOException, ServerException {
        Command cmd = new Command("ALLO", String.valueOf(size));
        Reply reply = null;
        try {
            reply = controlChannel.execute(cmd);
        } catch (UnexpectedReplyCodeException urce) {
            throw ServerException.embedUnexpectedReplyCodeException(urce);
        } catch (FTPReplyParseException rpe) {
            throw ServerException.embedFTPReplyParseException(rpe);
        }
    }

    // basic compatibility API
    
    public long size(String filename) throws IOException, ServerException {
        return getSize(filename);
    }
    
    public Date lastModified(String filename)
        throws IOException, ServerException {
        return getLastModified(filename);
    }
    
    public void get(String remoteFileName, File localFile)
        throws IOException, ClientException, ServerException {
        DataSink sink = new DataSinkStream(new FileOutputStream(localFile));
        get(remoteFileName, sink, null);
    }
    
    public void put(File localFile, String remoteFileName, boolean append)
        throws IOException, ServerException, ClientException {
        DataSource source =
            new DataSourceStream(new FileInputStream(localFile));
        put(remoteFileName, source, null, append);
    }
    
    /**
     * Enables/disables passive data connections. 
     * 
     * @param passiveMode if true passive connections will be
     *        established. If false, they will not.
     */
    public void setPassiveMode(boolean passiveMode)
        throws IOException, ClientException, ServerException {
        if (passiveMode) {
            setPassive();
            setLocalActive();
        } else {
            setLocalPassive();
            setActive();
        }
    }
    
    public boolean isPassiveMode() {
        return (this.session.serverMode == session.SERVER_PASSIVE);
    }
    
    public boolean isActiveMode() {
        return (this.session.serverMode == session.SERVER_ACTIVE);
    }
    
} //FTPClient
