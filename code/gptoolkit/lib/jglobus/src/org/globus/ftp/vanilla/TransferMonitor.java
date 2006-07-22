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

import org.globus.ftp.MarkerListener;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.PerfMarker;
import org.globus.ftp.GridFTPRestartMarker;
import org.globus.ftp.exception.ServerException;
import org.globus.ftp.exception.UnexpectedReplyCodeException;
import org.globus.ftp.exception.FTPReplyParseException;

import java.io.InterruptedIOException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransferMonitor extends Thread {

    public final static int 
        LOCAL = 1,
        REMOTE = 2;
    private int side; // source or dest 

    private Log logger = null;

    private int maxWait;
    private int ioDelay;

    private BasicClientControlChannel controlChannel;
    private TransferState transferState;
    private MarkerListener mListener;

    private TransferMonitor other;

    private boolean abortable;
    private Flag aborted = new Flag();

    public TransferMonitor(BasicClientControlChannel controlChannel, 
                           TransferState transferState, 
                           MarkerListener mListener,
                           int maxWait,
                           int ioDelay,
                           int side) {
        logger =  LogFactory.getLog(TransferMonitor.class.getName() + 
                                    ((side == LOCAL) ? ".Local" : ".Remote")); 
        this.controlChannel = controlChannel;
        this.transferState = transferState;
        this.mListener = mListener;
        this.maxWait = maxWait;
        this.ioDelay = ioDelay;
        abortable = true;
        aborted.flag = false;
        this.side = side;
    }

    /**
       In this class, each instance gets a separate logger which is
       assigned the name in the constructor.
       This name is in the form "...GridFTPClient.thread host:port". 
       @return the logger name.
     **/
    public String getLoggerName() {
        return logger.toString();
    }

    public void setOther(TransferMonitor other) {
        this.other = other;
    }

    /**
     * Abort the tpt transfer 
     * but do not close resources
     */
    public void abort() {
        logger.debug("abort");
        
        if (!abortable) {
            return;
        }

        controlChannel.abortTransfer();
        interrupt();
        
        aborted.flag = true;
    }

    public void run() {
        
        try {
            // if the other thread had already terminated
            // with an error, behave as if it happened just now.
            if (transferState.hasError()) {
                logger.debug("the other thread terminated before this one started.");
                throw new InterruptedException();
            }

            logger.debug("waiting for 1st reply;  maxWait = " + 
                         maxWait + ", ioDelay = " + ioDelay);
            this.controlChannel.waitFor(aborted,
                                        ioDelay,
                                        maxWait);

            logger.debug("reading first reply");
            Reply firstReply = controlChannel.read();

            // 150 Opening BINARY mode data connection.
            // or
            // 125 Data connection already open; transfer starting
            if (Reply.isPositivePreliminary(firstReply)) {
                transferState.transferStarted();
                logger.debug("first reply OK: " + firstReply.toString());

                for(;;) {
          
                    logger.debug("reading next reply");
                    this.controlChannel.waitFor(aborted,
                                                ioDelay);                   
                    logger.debug("got next reply");
                    Reply nextReply = controlChannel.read();

                    //perf marker
                    if (nextReply.getCode() == 112) {
                        logger.debug("marker arrived: " + nextReply.toString());
                        if (mListener != null) {   
                            mListener.markerArrived(
                                    new PerfMarker(nextReply.getMessage()));
                        }
                            continue;
                    }

                    //restart marker
                    if (nextReply.getCode() == 111) {
                        logger.debug("marker arrived: " + nextReply.toString());
                        if (mListener != null) {
                            mListener.markerArrived(
                                    new GridFTPRestartMarker(
                                            nextReply.getMessage()));
                        }
                        continue;
                    }
                    
                    //226 Transfer complete
                    if (nextReply.getCode() == 226) {
                        abortable = false;
                        logger.debug("transfer complete: " + nextReply.toString());
                        break;
                    }
                 
                    // any other reply
                    logger.debug("unexpected reply: " + nextReply.toString());
                    logger.debug("exiting the transfer thread");
                    ServerException e = ServerException.embedUnexpectedReplyCodeException(
                            new UnexpectedReplyCodeException(nextReply),
                            "Server reported transfer failure");
                    
                    transferState.transferError(e);
                    other.abort();
                    break;
                }
                
            } else {    //first reply negative
                logger.debug("first reply bad: " + firstReply.toString());
                logger.debug("category: " + firstReply.getCategory());
                abortable = false;
                ServerException e = ServerException.embedUnexpectedReplyCodeException(
                        new UnexpectedReplyCodeException(firstReply));

                transferState.transferError(e);
                other.abort();
            }

            logger.debug("thread dying naturally");

        } catch (InterruptedException td) { 
            //other transfer thread called abort()
            logger.debug("thread dying of InterruptedException.");
            transferState.transferError(td);
        } catch (InterruptedIOException td) {
            //other transfer thread called abort() which occurred
            //while this thread was performing IO
            logger.debug("thread dying of InterruptedIOException.");
            transferState.transferError(td);
        } catch (IOException e) {
            logger.debug("thread dying of IOException");
            transferState.transferError(e);
            other.abort();

        } catch (FTPReplyParseException rpe) {
            logger.debug("thread dying of FTPReplyParseException");
            ServerException se = ServerException.embedFTPReplyParseException(rpe);
            transferState.transferError(se);
            other.abort();
        } catch (ServerException e) {
            logger.debug("thread dying of timeout");
            transferState.transferError(e);
            other.abort();
        } finally {
            transferState.transferDone();
        }
    }

}
