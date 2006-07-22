import os
import sys

from HyperText.HTML import *

import pyARC
import LapUtils

import Lap
import LapTemplates

from WebKit.Page import Page

LapHeaderTextColor = "255,255,255"
LapHeaderColor = "123, 154, 192"
LapTextColor = "0,0,0"
LapDefaultColor = "218, 227, 237"

def pleaseWaitBox(page, message):

	params = {
		"message":message
	}

	page.write(LapTemplates.pleaseWaitTemplate % params)	

def messageBox(page, message, title="Message", gotoPage="", caption="OK"):
	
	divMessageBox = DIV(id="messageBoxWindow")
	divMessageBoxCaption = DIV(id="messageBoxCaption")
	divMessageBoxContent = DIV(id="messageBoxContent")
	
	divMessageBox.append(divMessageBoxCaption)
	divMessageBox.append(divMessageBoxContent)
	
	divMessageBoxCaption.append(title)
	
	form = FORM(INPUT(type="submit", value="OK"), method="Post", action=gotoPage)
	table = TABLE(TR(TD(message), id="messageBoxTDText"),TR(TD(form)), id="messageBoxContentTable")
	divMessageBoxContent.append(table)
	
	page.writeln(divMessageBox)

def messageBoxYesNo(page, message, title="Question", yesMethod = "", noMethod=""):
	
	divMessageBox = DIV(id="messageBoxWindow")
	divMessageBoxCaption = DIV(id="messageBoxCaption")
	divMessageBoxContent = DIV(id="messageBoxContent")
	
	divMessageBox.append(divMessageBoxCaption)
	divMessageBox.append(divMessageBoxContent)
	
	divMessageBoxCaption.append(title)
	
	form = FORM(
		INPUT(type="submit", value="Yes", name=yesMethod),
		INPUT(type="submit", value="No", name=noMethod),
		method="Post"
	)
	table = TABLE(TR(TD(message), id="messageBoxTDText"),TR(TD(form)), id="messageBoxContentTable")
	divMessageBoxContent.append(table)
	
	page.writeln(divMessageBox)	
	
class Menu:
	def __init__(self, page, title, width):
		self.page = page
		self.title = title
		self.width = width
		self.menuItems = []
		self.indentLevel = 0
		self.indentWidth = 10
		
	def indent(self):
		self.indentLevel = self.indentLevel + 1
		
	def unindent(self):
		if self.indentLevel>0:
			self.indentLevel = self.indentLevel - 1
			
	def addItem(self, caption, href=""):
		self.menuItems.append((caption, href, self.indentLevel))

	def setTitle(self, title):
		self.title = title
		
	def setIndentWidth(self, indentWidth):
		self.indentWidth = indentWidth

	def clear(self):
		self.menuItems = []

	def render(self):
		wl = self.page.writeln
		w = self.page.write
		
		tableStyle  = "font-family: tahoma; font-size: 10pt; background-color: rgb(255, 255, 255); margin-left: auto; margin-right: auto; width: %(width)s; text-align: left;"
		headerStyle = "font-weight: bold; color: rgb(255, 255, 255); text-align: center; background-color: rgb(123, 154, 192); vertical-align: top;"
		cellStyle   = "background-color: rgb(218, 227, 237);"
		
		widthString = "%d%s" % (self.width, "%")
		table = TABLE(style=tableStyle, width=widthString, cellspacing=0, cellpadding=2)
		tableBody = TBODY()
		table.append(tableBody)
		tableBody.append(TR(TD(self.title, style=headerStyle)))
		
		for menuItem in self.menuItems:
			if menuItem[1]!="":
				tableBody.append(TR(TD(DIV(A(menuItem[0],href=menuItem[1]),style="margin-left: %dpx;" % (menuItem[2]*self.indentWidth)),style=cellStyle)))
			else:
				tableBody.append(TR(TD(menuItem[0],BR(),style=cellStyle)))
		
		w(table)
		w(BR())


class Form:
	def __init__(self, page, name, action, caption="Noname", width=50, rows=5, cols=5, contentID="formContent"):
		self.page = page
		self.caption = caption
		self.width = width
		self.action = action
		self.name = name
		self.submitButtonName = "_action_submit"
		self.submitButtonValue = "Submit"
		self.controls = []
		self.buttons = []
		self.haveSubmit = True
		self.tableLayout = False
		
		self.solidColor = False
		
		self.contentID = "formContent"
		self.windowID = "formWindow"

		self.setTableSize(rows, cols)
		
	def setSolidColor(self, flag):
		self.solidColor = flag
		
	def setContentID(self, id):
		self.contentID = id
		
	def setWindowID(self, id):
		self.windowID = id
		
	def getSolidColor(self):
		return self.solidColor
				
	def setTableSize(self, rows, cols):

		self.table = []
		self.tableColors = []
		self.tableControls = []
		
		self.rows = rows
		self.cols = cols
		
		for r in range(0,rows):
			self.table.append([])
			self.tableColors.append([])
			self.tableControls.append([])
			for c in range(0,cols):
				self.table[r].append("")
				self.tableColors[r].append("")
				self.tableControls[r].append(None)

	def addText(self, label, name, value = "", descurl = "", row=-1, col=-1):
		controlParams = (label, "text", name, value, descurl, "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams

	def addPassword(self, label, name, value = "", descurl = "", row=-1, col=-1):
		controlParams = (label, "password", name, value, descurl, "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams

	def addReadonlyText(self, label, name, value = "", row=-1, col=-1):
		controlParams = (label, "text", name, value, "", "readonly")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams

	def addTextArea(self, label, name, value = "", descurl = "", row=-1, col=-1):
		controlParams = (label, "textarea", name, value, descurl, "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams			
			
	def addHidden(self, label, name, value = "", row=-1, col=-1):
		controlParams = (label, "hidden", name, value, "", "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams

	def addButton(self, label, name, row=-1, col=-1):
		controlParams = (label, "button", name, label, "", "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams

	def addCheck(self, label, name, descurl = "", row=-1, col=-1):
		controlParams = (label, "checkbox", name, descurl, "", "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams

	def addRadio(self, label, name, value="", row=-1, col=-1):
		controlParams = (label, "radio", name, value, "")
		self.controls.append(controlParams)
		print row, ", ", col
		if row>=0:
			self.tableControls[row][col] = controlParams

	def addRadioWithDesc(self, label, name, value="", descurl = "", row=-1, col=-1):
		controlParams = (label, "radio", name, value, descurl, "")
		self.controls.append(controlParams)
		print row, ", ", col
		if row>=0:
			self.tableControls[row][col] = controlParams			
			
	def addSelect(self, label, name, values, selected, descurl = "", row=-1, col=-1):

		sel = Select(values, selected, name=name)
		
		#sel = SELECT(OPTION("yo", value="yo", selected=0), OPTION("zo", value="zo", selected=1), name="zos")

		controlParams = (label, "select", sel, descurl, "", "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams			

	def addTextArea(self, label, name, value = "", descurl = "", row=-1, col=-1):
		controlParams = (label, "textarea", name, value, descurl, "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams										
			
	def addFile(self, label, name, value="", descurl = "", row=-1, col=-1):
		controlParams = (label, "file", name, descurl, "", "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams
			
	def addNormalText(self, label, row = -1, col=-1):
		controlParams = (label, "plaintext", "", "", "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams

	def addNormalTextWithDesc(self, label, descurl = "", row = -1, col=-1):
		controlParams = (label, "plaintext", descurl, "", "", "")
		self.controls.append(controlParams)
		if row>=0:
			self.tableControls[row][col] = controlParams			
			
	def addSeparator(self):
		self.controls.append(("", "-", "", "", ""))

	def setAction(self, action):
		self.action = action

	def setName(self, name):
		self.name = name
		
	def addFormButton(self, label, name = ""):
		self.buttons.append((label, name))

	def setSubmitButton(self, name, value):
		self.submitButtonName = name
		self.submitButtonValue = value
		
	def setHaveSubmit(self, flag):
		self.haveSubmit = flag
		
	def setTableLayout(self, flag):
		self.tableLayout = flag

	def render(self):
		
		form = FORM(method="Post", enctype="multipart/form-data", action=self.action)
		formWindow = DIV(id=self.windowID)
		
		formCaption = DIV(self.caption, id="formCaption")
		formContent = DIV(id=self.contentID)
		
		formWindow.append(formCaption)
		formWindow.append(formContent)
		
		formContent.append(form)
		
		table = TABLE(
			klass="lapform"
			)
		
		form.append(table)
				
		tableBody = TBODY()
		tableHeader = THEAD()
		tableFoot = TFOOT()
		
		#table.append(tableHeader)
		table.append(tableBody)
		table.append(tableFoot)
		
		if not self.tableLayout:
			tableHeader.append(TR(TD(self.caption, colspan=3)))
		else:
			tableHeader.append(TR(TD(self.caption, colspan=self.cols)))

		#if not self.tableLayout:
		#	tableHeader.append(TR(TD(self.caption, style=headerStyle, colspan=3)))
		#else:
		#	tableHeader.append(TR(TD(self.caption, style=headerStyle, colspan=self.cols)))

		if not self.tableLayout:
			tableBody.append(TR(SMALL(TD())))
			
		if not self.tableLayout:
			for control in self.controls:
				if control[1]<>"-":
					if control[1]<>"hidden":
						if control[1]=="radio":
							if control[4] == "":
								flabel = control[0]
							else:
								flabel = Href(control[4], control[0], target="new")
							tableBody.append(
								TR(
									TD(INPUT(type=control[1], name=control[2], value=control[3]), id="tdCaption"),
									TD(),
									TD(flabel, id="tdControl")
								)
							)
						elif control[1]=="checkbox":
							if control[4] == "":
								flabel = control[0]
							else:
								flabel = Href(control[4], control[0], target="new")							
							tableBody.append(
								TR(
									TD(INPUT(type=control[1], name=control[2], value=control[3]), id="tdCaption"),
									TD(),
									TD(flabel, id="tdControl")
								)
							)
						elif control[1]=="plaintext":
							if control[4] == "":
								flabel = control[0]
							else:
								flabel = Href(control[4], control[0], target="new")							
							tableBody.append(
								TR(
									TD(DIV(flabel, align="left", style="margin-left:10px"), id="tdCaption", colspan=3)
									#TD(),
									#TD()
								)
							)		
						elif control[1]=="textarea":
							if control[4] == "":
								flabel = control[0]
							else:
								flabel = Href(control[4], control[0], target="new")							
							tableBody.append(
								TR(
									TD(flabel, id="tdCaption"),
									TD(),								
									TD(TEXTAREA(control[3],
									 						name=control[2],
															cols=38,
															rows=4), 
															id="tdControl")
								)
							)						
						elif control[1]=="select":
							if control[3] == "":
								flabel = control[0]
							else:
								flabel = Href(control[3], control[0], target="new")									
							tableBody.append(
								TR(
									TD(flabel, id="tdCaption"),
									TD(),					
									TD(control[2])
								)
							)
						elif control[1]=="file":
							if control[3] == "":
								flabel = control[0]
							else:
								flabel = Href(control[3], control[0], target="new")									
							tableBody.append(
								TR(
									TD(flabel, id="tdCaption"),
									TD(),					
									TD(INPUT(type=control[1], name=control[2], size=40, value=control[2]), id="tdControl")
								)
							)							
						else:
							if control[5]=="":
								if control[4] == "":
									flabel = control[0]
								else:
									flabel = Href(control[4], control[0], target="new")
								tableBody.append(
									TR(
										TD(flabel, id="tdCaption"),
										TD(),
										TD(INPUT(type=control[1], name=control[2], size=40, value=control[3]), id="tdControl")
									)
								)
							else:
								if control[4] == "":
									flabel = control[0]
								else:
									flabel = Href(control[4], control[0], target="new")										
								tableBody.append(
									TR(
										TD(flabel, id="tdCaption"),
										TD(),
										TD(INPUT(
											type=control[1],
											name=control[2],
											size=40,
											value=control[3],
											readonly=1
											), id="tdControl"
										)
									)
								)
					else:
						tableBody.append(INPUT(type=control[1], name=control[2], value=control[3]))
							
				else:
					tableBody.append(TR(SMALL(SMALL(TD()))))
		else:
			
			cellID    = "cellStyle1"
			cellIDAlt = "cellStyle2"

			row = 0
			col = 0
			
			AltColor = False
			
			while row<self.rows:
				
				tableRow = TR()
				tableBody.append(tableRow)
				while col<self.cols:
					
					control = self.tableControls[row][col]
					
					if not self.solidColor:
						if AltColor:
							currentID = cellID
						else:
							currentID = cellIDAlt
					else:
						currentID = cellID
					
					if control!=None:
						if control[1]<>"hidden":
							if control[1]=="radio":
								tableRow.append(
										TD(INPUT(type=control[1], name=control[2], value=control[3]), id=currentID)
								)
							elif control[1]=="checkbox":
								tableRow.append(
										TD(INPUT(type=control[1], name=control[2], value=control[3]), id=currentID)
								)						
							elif control[1]=="plaintext":
								tableRow.append(
										TD(control[0], id=currentID)
								)
							else:
								if control[4]=="":
									tableRow.append(
											TD(INPUT(type=control[1], name=control[2], value=control[3]), style=currentStyle)
									)
								else:
									tableRow.append(
											TD(INPUT(
												type=control[1],
												name=control[2],
												value=control[3],
												readonly=1
												), id=currentID
											)
									)
						else:
							tableBody.append(INPUT(type=control[1], name=control[2], value=control[3]))
					else:
						tableRow.append(TD(id=currentID))
											
					col = col + 1
							
				col = 0
				row = row + 1
				AltColor = not AltColor
				
		tableFoot.append(TR(TD(height="10px")))

		buttonRow = TR()
		if not self.tableLayout:
			buttonData = TD(colspan=3, style="text-align: center")
		else:
			buttonData = TD(colspan=self.cols, style="text-align: center")
			
		buttonRow.append(buttonData)
		tableFoot.append(buttonRow)
		
		for button in self.buttons:
			buttonData.append(INPUT(type="submit", name=button[1], value=button[0]))
		if self.haveSubmit:
			buttonData.append(INPUT(type="submit", name=self.submitButtonName, value=self.submitButtonValue))
			
		buttonData.append(INPUT(type="reset", name="btnReset", value = "Reset"))

		#tableFoot.append(TR(TD(height="10px")))
		
		self.page.write(formWindow)
		
		
class Table:
	def __init__(self, page, rows=2, cols=2, caption=""):
		self.page = page

		self.params = {
			"width":400,
		}

		self.table = []
		self.tableColors = []
		
		self.caption = caption
		
		self.rows = rows
		self.cols = cols

		for r in range(0,rows):
			self.table.append([])
			self.tableColors.append([])
			for c in range(0,cols):
				self.table[r].append("")
				self.tableColors[r].append("")

	def setItem(self, row, col, value):
		self.table[row][col] = value
		
	def setColor(self, row, col, color):
		self.tableColors[row][col] = color

	def render(self):

		rowNumber = 0

		wl = self.page.writeln
		w = self.page.write
		
		cellID    = "cellStyle1"
		cellIDAlt = "cellStyle2"
		
		table = TABLE(klass="laptable", cellpadding=3, cellspacing=2)
	
		row = 0
		col = 0
		
		AltColor = False
		
		cap = False
		if self.caption != "":
			table.append(TR(TD(self.caption, colspan=self.cols, id="tdCaption")))
			cap = True
			
		while row<self.rows:
			
			tableRow = TR()
			table.append(tableRow)
			while col<self.cols:
				if row == 0 and not cap:
					tableRow.append(TD(self.table[row][col], id="tdCaption"))
				else:
					if self.tableColors[row][col]=="":
						if AltColor:
							tableRow.append(TD(self.table[row][col], id=cellID))
						else:
							tableRow.append(TD(self.table[row][col], id=cellIDAlt))
					else:
						tableRow.append(TD(self.table[row][col], style="background-color: rgb(%s)" % self.tableColors[row][col]))
						
				col = col + 1
						
			col = 0
			row = row + 1
			AltColor = not AltColor

		w(CENTER(table))
		
