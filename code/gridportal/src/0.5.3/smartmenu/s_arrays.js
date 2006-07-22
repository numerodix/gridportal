// USE WORDWRAP AND MAXIMIZE THE WINDOW TO SEE THIS FILE
// v5

// === 1 === EXTRAS
s_hideTimeout=0;//1000=1 second
s_subShowTimeout=0;//if <=100 the menus will function like SM4.x
s_subMenuOffsetX=5;//pixels (if no subs, leave as you like)
s_subMenuOffsetY=5;
s_keepHighlighted=true;
s_autoSELECTED=false;//make the item linking to the current page SELECTED
s_autoSELECTEDItemsClickable=false;//look at IMPORTANT NOTES 1 in the Manual
s_autoSELECTEDTree=true;//look at IMPORTANT NOTES 1 in the Manual
s_autoSELECTEDTreeItemsClickable=true;//look at IMPORTANT NOTES 1 in the Manual
s_scrollingInterval=30;//scrolling for tall menus
s_rightToLeft=false;
s_hideSELECTsInIE=true;//look at IMPORTANT HOWTOS 7 in the Manual


// === 2 === Default TARGET for all the links
// for navigation to frame, calling functions or
// different target for any link look at
// IMPORTANT HOWTOS 1 NOTES in the Manual
s_target='self';//(newWindow/self/top)


// === 3 === STYLESHEETS- you can define different arrays and then assign
// them to any menu you want with the s_add() function
s_CSSTop=[
'#4A49A8',	// BorderColorDOM ('top right bottom left' or 'all')
'#4A49A8',	// BorderColorNS4
0,		// BorderWidth
'#eff3ff',	// BgColor
0,		// Padding
'#eff3ff',	// ItemBgColor
'#5a759c',	// ItemOverBgColor
'#4a557b',	// ItemFontColor
'#FFFFFF',	// ItemOverFontColor
'verdana,arial,helvetica,sans-serif',	// ItemFontFamily
'12px',		// ItemFontSize (css)
'1',		// ItemFontSize Netscape4 (look at KNOWN BUGS 3 in the Manual)
'normal',		// ItemFontWeight (bold/normal)
'left',		// ItemTextAlign (left/center/right)
5,		// ItemPadding
0,		// ItemSeparatorSize
'#8A8CCC',	// ItemSeparatorColor
'',		// IEfilter (look at Samples\IE4(5.5)Filters dirs)
false,				// UseSubImg
'../../images/arrow4.gif',		// SubImgSrc
'../../images/arrow4over.gif',	// OverSubImgSrc
7,				// SubImgWidth
7,				// SubImgHeight
9,				// SubImgTop px (from item top)
'#8A8CCC',			// SELECTED ItemBgColor
'#FFFFFF',			// SELECTED ItemFontColor
'../../images/arrow4over.gif',	// SELECTED SubImgSrc
true,				// UseScrollingForTallMenus
'../../images/scrolltop.gif',	// ScrollingImgTopSrc
'../../images/scrollbottom.gif',	// ScrollingImgBottomSrc
68,				// ScrollingImgWidth
12,				// ScrollingImgHeight
'normal',		// ItemClass (css)
'over',			// ItemOverClass (css)
'selected',		// SELECTED ItemClass (css)
0,		// ItemBorderWidth
'#CBCBEF',	// ItemBorderColor ('top right bottom left' or 'all')
'#FFFFFF',	// ItemBorderOverColor ('top right bottom left' or 'all')
'#FFFFFF',	// SELECTED ItemBorderColor ('top right bottom left' or 'all')
0,		// ItemSeparatorSpacing
''		// ItemSeparatorBgImage
];


// FUNCTIONS FOR GETTING MENU DIMENTIONS AND POSITION
function s_getDim(name,a){
var o=s_nS4?document.layers["s_m"+s_nr(name)]:s_getO("s_m"+s_nr(name));
if(!o)
	return 0;
if(s_nS4)
	return a=="h"?o.clip.height:o.clip.width;
if(a=="h")
	return s_iE&&!s_mC?o.clientHeight:o.offsetHeight?o.offsetHeight:o.style.pixelHeight;
return s_iE&&!s_mC?o.clientWidth:s_oP7m?o.style.w:o.offsetWidth;
}

function s_getPos(name,a){
var o=s_nS4?document.layers["s_m"+s_nr(name)]:s_getO("s_m"+s_nr(name));
if(!o)
	return 0;
if(!s_nS4)
	o=o.style;
return a=="l"?parseInt(o.left):parseInt(o.top);
}
// FUNCTIONS FOR GETTING MENU DIMENTIONS AND POSITION


// CODE USED FOR REPOSITIONING PERMANENT MENUS WHILE PAGE LOADING
function s_whilePageLoading(){if(typeof s_ML=="undefined"){setTimeout("s_whilePageLoading()",1000);return};var px=s_oP7m||s_nS4?0:"px",os=null,x,y,i,S;for(i=0;i<s_P.length;i++){S=s_[s_P[i]][0];if(typeof(S.T)=="number"&&typeof(S.L)=="number")continue;os=s_nS4?document.layers["s_m"+s_P[i]]:s_getOS("s_m"+s_P[i]);os.left=eval(S.L)+px;os.top=eval(S.T)+px};if(typeof s_Bl=="undefined")setTimeout("s_whilePageLoading()",1000)};s_whilePageLoading();s_ol=window.onload?window.onload:function(){};window.onload=function(){setTimeout('s_Bl=1',3000);s_ol()}
// CODE USED FOR REPOSITIONING PERMANENT MENUS WHILE PAGE LOADING


// EXAMPLE FUNCTION FOR CENTERING A HORIZONTAL MENU
function s_centerHorizontalMenu(){
var windowWidth,menuWidth=0,scrollBarFix,i;

scrollBarFix=s_nS&&!document.body.clientWidth?15:s_nS4&&innerWidth<document.width?16:0;

windowWidth=s_iE?s_dE.clientWidth:s_nS&&document.body.clientWidth&&!s_sF?s_dE.clientWidth?s_dE.clientWidth:document.body.clientWidth:s_oP7?document.body.clientWidth:innerWidth-scrollBarFix;

for(i=0;i<arguments.length;i++){
	menuWidth+=s_getDim(arguments[i],"w");
	menuWidth-=i<arguments.length-1?1:0; // substract border overlapping
}

return parseInt(windowWidth/2-menuWidth/2);
}
// EXAMPLE FUNCTION FOR CENTERING A HORIZONTAL MENU


// === 4 === MENU DEFINITIONS
s_add(
{N:'H1',	// NAME
LV:1,		// LEVEL
T:120,		// TOP
L:0,		//L:'s_centerHorizontalMenu("H1","H2","H3","H4","H5","H6","H7")',	// LEFT
P:true,		// menu is PERMANENT (you can only set true if this is LEVEL 1 menu)
S:s_CSSTop	// STYLE Array to use for this menu
},
[
{Show:'information',U:'',T:'&nbsp; Information &nbsp;'}
]
);

	s_add(
	{N:'information',LV:2,MinW:130,T:'s_getDim("H1","h")+s_getPos("H1","t")-1',L:'s_getPos("H1","l")',P:false,S:s_CSSTop},
	[
	{U:'DocGettingStarted',T:'Getting Started'},
	{U:'DocUserGuide',T:'User\'s Guide'},
	{U:'DocTar',T:'Handling .tar.gz-files (7-zip)'}
	]
	);

s_add(
{N:'H2',LV:1,T:'s_getPos("H1","t")',L:'s_getPos("H1","l")+s_getDim("H1","w")-1',P:true,S:s_CSSTop},
[
{Show:'authorization',U:'',T:'&nbsp; Authorization &nbsp;'}
]
);

	s_add(
	{N:'authorization',LV:2,MinW:130,T:'s_getDim("H2","h")+s_getPos("H2","t")-1',L:'s_getPos("H2","l")',P:false,S:s_CSSTop},
	[
	{U:'',T:'Generate a certificate request...'},
	{U:'',T:'Requesting resource access...'}
	]
	);

s_add(
{N:'H3',LV:1,T:'s_getPos("H1","t")',L:'s_getPos("H2","l")+s_getDim("H2","w")-1',P:true,S:s_CSSTop},
[
{Show:'session',U:'',T:'&nbsp; Session &nbsp;'}
]
);

	s_add(
	{N:'session',LV:2,MinW:130,T:'s_getDim("H3","h")+s_getPos("H3","t")-1',L:'s_getPos("H3","l")',P:false,S:s_CSSTop},
	[
	{U:'ShowUserPage',T:'Information...'},
	{U:'LoginPageDummy',T:'Log in...'},
	{U:'LogoutPage',T:'Log out...'}
	]
	);

s_add(
{N:'H4',LV:1,T:'s_getPos("H1","t")',L:'s_getPos("H3","l")+s_getDim("H3","w")-1',P:true,S:s_CSSTop},
[
{Show:'preferences',U:'',T:'&nbsp; Preferences &nbsp;'}
]
);

	s_add(
	{N:'preferences',LV:2,MinW:130,T:'s_getDim("H4","h")+s_getPos("H4","t")-1',L:'s_getPos("H4","l")',P:false,S:s_CSSTop},
	[
	{U:'',T:'User...'},
	{U:'',T:'Portal...'},
	{U:'',T:'Grid...'}
	]
	);

s_add(
{N:'H5',LV:1,T:'s_getPos("H1","t")',L:'s_getPos("H4","l")+s_getDim("H4","w")-1',P:true,S:s_CSSTop},
[
{Show:'create',U:'',T:'&nbsp; Create &nbsp;'}
]
);

	s_add(
	{N:'create',LV:2,MinW:130,T:'s_getDim("H5","h")+s_getPos("H5","t")-1',L:'s_getPos("H5","l")',P:false,S:s_CSSTop},
	[
	{Show:'blast',U:'',T:'BLAST &raquo;'},
	{U:'MatlabJobPage',T:'Matlab...'}
	]
	);

		s_add(
		{N:'blast',LV:3,MinW:130,T:'',L:'',P:false,S:s_CSSTop},
		[
		{U:'BlastJobPageblastp',T:'blastp...'},
		{U:'BlastJobPageblastn',T:'blastn...'},
		{U:'BlastJobPageblastx',T:'blastx...'},
		{U:'BlastJobPagetblastn',T:'tblastn...'},
		{U:'BlastJobPagetblastx',T:'tblastx...'},
		{U:'BlastJobPagemegablast',T:'megablast...'}
		]
	);

s_add(
{N:'H6',LV:1,T:'s_getPos("H1","t")',L:'s_getPos("H5","l")+s_getDim("H5","w")-1',P:true,S:s_CSSTop},
[
{Show:'manage',U:'',T:'&nbsp; Manage &nbsp;'}
]
);

	s_add(
	{N:'manage',LV:2,MinW:130,T:'s_getDim("H6","h")+s_getPos("H6","t")-1',L:'s_getPos("H6","l")',P:false,S:s_CSSTop},
	[
	{U:'ManageJobPage',T:'Local jobs...'},
	{U:'PleaseWaitPage?URL=ManageGridJobPage&Message=Querying for jobs...',T:'Jobs submitted to grid...'},
	{U:'PleaseWaitPage?URL=SyncJobsPage&Message=Syncronising job list...',T:'Synchronize job list'},
	{U:'PleaseWaitPage?URL=JobStatus&Message=Performing status query...',T:'Monitor running jobs'}
	]
	);

s_add(
{N:'H7',LV:1,T:'s_getPos("H1","t")',L:'s_getPos("H6","l")+s_getDim("H6","w")-1',P:true,S:s_CSSTop},
[
{Show:'about',U:'',T:'&nbsp; About... &nbsp;'}
]
);

	s_add(
	{N:'about',LV:2,MinW:130,T:'s_getDim("H7","h")+s_getPos("H7","t")-1',L:'s_getPos("H7","l")',P:false,S:s_CSSTop},
	[
	{U:'WelcomePage',T:'GRIDportal...'}
	]
	);
