/* Generated By:JavaCC: Do not edit this line. RSLParserConstants.java */
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
package org.globus.rsl;

public interface RSLParserConstants {

  int EOF = 0;
  int EQUAL = 6;
  int NOT_EQUAL = 7;
  int GREATER_THAN = 8;
  int GREATER_THAN_EQUAL = 9;
  int LESS_THAN = 10;
  int LESS_THAN_EQUAL = 11;
  int AND = 12;
  int OR = 13;
  int MULTI = 14;
  int RPAREN = 15;
  int LPAREN = 16;
  int VARSTART = 17;
  int VARIABLES = 18;
  int VARIABLES_DQUOTE = 19;
  int VARIABLES_SQUOTE = 20;
  int CHARACTER = 21;
  int DIGIT = 22;
  int OTHER_CHAR = 23;
  int UNQUOTED_LITERAL = 24;
  int DOUBLE_QUOTED_LITERAL = 25;
  int SINGLE_QUOTED_LITERAL = 26;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\r\"",
    "\"\\n\"",
    "<token of kind 5>",
    "\"=\"",
    "\"!=\"",
    "\">\"",
    "\">=\"",
    "\"<\"",
    "\"<=\"",
    "\"&\"",
    "\"|\"",
    "\"+\"",
    "\"(\"",
    "\")\"",
    "\"$(\"",
    "<VARIABLES>",
    "<VARIABLES_DQUOTE>",
    "<VARIABLES_SQUOTE>",
    "<CHARACTER>",
    "<DIGIT>",
    "<OTHER_CHAR>",
    "<UNQUOTED_LITERAL>",
    "<DOUBLE_QUOTED_LITERAL>",
    "<SINGLE_QUOTED_LITERAL>",
    "\"#\"",
  };

}