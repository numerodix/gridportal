from WebKit.HTTPServlet import HTTPServlet


class index(HTTPServlet):

	def respond(self, trans):
		trans.application().forward(trans, '/WelcomePage' + trans.request().extraURLPath() )
#		trans.response().sendRedirect('Welcome')
