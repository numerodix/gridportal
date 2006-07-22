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
package org.globus.ftp.dc;
import org.globus.ftp.extended.GridFTPServerFacade;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StripeContextManager {

	static Log logger =
		LogFactory.getLog(StripeContextManager.class.getName());

	protected int stripes;
	protected StripeTransferContext contextList[];
	protected int stripeQuitTokens = 0;
	protected Object contextQuitToken = new Object();

	public StripeContextManager(int stripes, 
				    SocketPool pool, 
				    GridFTPServerFacade facade) {
		this.stripes = stripes;
		contextList = new StripeTransferContext[stripes];
		for (int i = 0; i < stripes; i++) {
			contextList[i] = new StripeTransferContext(this);
			contextList[i].setSocketPool(pool);
			contextList[i].setTransferThreadManager(
			    facade.createTransferThreadManager());
		}
	}

	/**
	   return number of stripes
	 **/
	public int getStripes() {
		return stripes;
	}

	public EBlockParallelTransferContext getStripeContext(int stripe) {
		return contextList[stripe];
	}

	public Object getQuitToken() {
		int i = 0;
		while (i < stripes) {
			logger.debug("examining stripe " + i);
			if (contextList[i].getStripeQuitToken() != null) {
				// obtained quit token from one stripe.
				stripeQuitTokens ++;
				logger.debug("obtained stripe quit token. Total = " + stripeQuitTokens + "; total needed = " + stripes);
			}
			i ++;
		}
		if (stripeQuitTokens == stripes) {
			// obtained quit tokens from all stripes.
			// ready to release the quit token. But make sure not to do it twice.
			// This section only returns non-nul the first time it is entered.
			if (contextQuitToken == null) {
			    logger.debug("not releasing the quit token.");
			} else {
			    logger.debug("releasing the quit token.");
			}
			Object myToken = contextQuitToken;
			contextQuitToken = null;
			return myToken;
		} else {
			// not all stripes ready to quit
			logger.debug("not releasing the quit token. ");
			return null;
		}
	}

	class StripeTransferContext extends EBlockParallelTransferContext {

		StripeContextManager mgr;

		public StripeTransferContext(StripeContextManager mgr) {
			this.mgr = mgr;
		}

		/**
		   @return non-null if this stripe received or sent all the EODs
		**/
		public Object getStripeQuitToken() {
			Object token = super.getQuitToken();
			StripeContextManager.logger.debug(
				(token != null) ? "stripe released the quit token" : "stripe did not release the quit token");
			return token;
		}

		/**
		   @return non-null if all EODs in all stripes have been transferred.
		**/
		public Object getQuitToken() {
			return mgr.getQuitToken();
		}
	}
}
