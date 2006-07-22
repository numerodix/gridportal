import os
import sys

from HyperText.HTML import *

import pyARC
import LapUtils

import Lap
import LapTemplates

from WebKit.Page import Page

transMenuInitTemplateStart = """<script language="javascript" src="./transmenu/transmenu.js"></script>
<script language="javascript">
function init() {
	if (TransMenu.isSupported()) {
		TransMenu.initialize();

"""
		

transMenuInitTemplateEnd = """
	}
}
</script>
"""

transMenuDeclarationStart = """<script language="javascript">
if (TransMenu.isSupported()) {
		var ms = new TransMenuSet(TransMenu.direction.down, 0, 0, TransMenu.reference.bottomLeft);
"""

transMenuDeclarationEnd = """TransMenu.renderAll();
}
</script>
"""

class MenuItem:
	def __init__(self, caption="defaultMenuItem", link="", hint=""):
		self.caption = caption
		self.link = link
		self.hint = hint
		self.subMenu = None
	
	def setCaption(self, caption):
		self.caption = caption
		
	def getCaption(self):
		return self.caption
	
	def setLink(self, link):
		self.link = link
		
	def getLink(self):
		return self.link
	
	def setHint(self, hint):
		self.hint = hint
		
	def getHint(self):
		return self.hint
	
	def setSubmenu(self, menu):
		self.subMenu = menu
		
	def getSubmenu(self):
		return self.subMenu
		
	def isSubmenu(self):
		return self.subMenu!=None

class Menu:
	def __init__(self, page, name="defaultMenu", caption="default", link="", slide="down", x=0, y=0, width=100, height=100):
		self.name = name
		self.slide = slide
		self.x = x
		self.y = y
		self.width = width
		self.height = height
		self.page = page
		self.hint = ""
		self.link = link
		self.caption = caption
		self.separator = False
		self.subMenu = False
		self.menuItems = []
		self.parentMenu = None
		
	def addMenuItem(self, menuItem):
		self.menuItems.append(menuItem)
		
	def addSubmenu(self, menuItem, menu):
		menuItem.setSubmenu(menu)
		self.menuItems.append(menuItem)
		
	def setSubmenu(self, flag):
		self.subMenu = flag
		
	def isSubmenu(self):
		return self.subMenu
	
	def setParentMenu(self, menu):
		self.parentMenu = menu
		
	def getParentMenu(self):
		return self.parentMenu
	
	def setSeparator(self, flag):
		self.separator = flag
		
	def isSeparator(self):
		return self.separator
	
	def setHint(self, hint):
		self.hint = hint
		
	def getHint(self):
		return self.hint
		
	def setLink(self, link):
		self.link = link
		
	def getLink(self):
		return self.link
	
	def setCaption(self, caption):
		self.caption = caption
		
	def getCaption(self):
		return self.caption
	
	def setName(self, name):
		self.name = name
	
	def getName(self):
		return self.name
	
	def setPosition(self, x, y):
		self.x = x
		self.y = y
		
	def setWidth(self, width):
		self.width = width
		
	def getLeft(self):
		return self.x
	
	def getTop(self):
		return self.y
	
	def getMenuItems(self):
		return self.menuItems
	
	def renderJavaScript(self, parentName = "", idx = -1):
		
		if parentName == "":
			parent = "ms"
		else:
			parent = parentName
		
		if parent == "ms":
			self.page.writeln('var var%(name)s = %(parentName)s.addMenu(document.getElementById("%(name)s"));' %
							  { "name":self.getName(), "parentName":parent} );
		else:
			self.page.writeln('var submenu = %(parentName)s.addMenu(%(parentName)s.items[%(idx)d]);' %
							  { "name":self.getName(), "parentName":parent, "idx":idx });
			
		menuCount = 0
		
		for menuItem in self.menuItems:
			if not menuItem.isSubmenu():
				if parent == "ms":
					self.page.writeln('var%(name)s.addItem("%(caption)s", "%(link)s");' %
									  {"name":self.getName(), "caption":menuItem.getCaption(), "link":menuItem.getLink()})
				else:
					self.page.writeln('submenu.addItem("%(caption)s", "%(link)s");' %
									  {"name":self.getName(), "caption":menuItem.getCaption(), "link":menuItem.getLink()})					
			else:
				self.page.writeln('var%(name)s.addItem("%(caption)s", "%(link)s");' %
								  {"name":self.getName(), "caption":menuItem.getCaption(), "link":menuItem.getLink()})
				menuItem.getSubmenu().renderJavaScript("var%s" % self.getName(), menuCount)
				
			menuCount = menuCount + 1
		
	
class MenuBar:
	def __init__(self, page):
		self.page = page
		self.menus = []
		self.subMenus = []
		
		self.left = 20
		self.top = 20
		self.width = 120
		
		self.fullWidth = False
		
	def setPosition(self, left, top):
		self.left = left
		self.top = top
		
	def setWidth(self, width):
		self.width = width
		
	def setFullWidth(self, flag):
		self.fullWidth = flag
		
	def getFullWidth(self):
		return self.fullWidth
		
	def addMenu(self, menu):
		self.menus.append(menu)
		
	def addSubmenu(self, menu):
		self.subMenus.append(menu)
		
	def addSeparator(self):
		menu = Menu(self.page)
		menu.setSeparator(True)
		self.addMenu(menu)
		
	def renderJavaScript(self):

		self.page.write(transMenuInitTemplateStart)
		
		for menu in self.menus:
			self.page.write("""\t\tvar%s.onactivate = function() { document.getElementById("%s").className='hover'; };\n""" % (menu.getName(), menu.getName()))
			self.page.write("""\t\tvar%s.ondeactivate = function() { document.getElementById("%s").className='';};\n""" % (menu.getName(), menu.getName()))
			
		self.page.write(transMenuInitTemplateEnd)
		
				
	
	def render(self):
		
		positionStyleString = "position:absolute;left:%dpx;top:%dpx;" % (self.left, self.top)
		
		if self.fullWidth:
			divMenu = DIV(id="menu", style=positionStyleString+"width:100%;")
		else:
			divMenu = DIV(id="menu", style=positionStyleString+"width:%d" % self.width)
			
		for menu in self.menus:
			
			if not menu.isSeparator():
			
				divMenu.append(
					A(
						menu.getCaption(),
						id=menu.getName(),
						href="#",
						title=menu.getHint()
					 )
				)
			else:
				divMenu.append(HR(size=1,noshade=0))
				divMenu.append("&nbsp;")
		
		self.page.write(divMenu)
		
	def renderBodyJavaScript(self):
		
		w = self.page.write
		
		w(transMenuDeclarationStart)
		
		for menu in self.menus:
			menu.renderJavaScript()	
			
		w(transMenuDeclarationEnd)	
	
class Window:
	
	def __init__(self, page):
		self.page = page
	
	def render(self):
		
		div = DIV(style="color:#000000; position:absolute; width:600px; left:200px; top:120px; border:1px dotted #7B9AC0")
		div.append(A("Hello, Window!"))
		self.page.writeln(div)
	
