/*
========================================
 SmartMenus v5.5 Script Core DOM
 Commercial License No.: UN-LICENSED
========================================
 Please note: THIS IS NOT FREE SOFTWARE.
 Available licenses for use:
 http://www.smartmenus.org/license.php
========================================
 (c)2005 ET VADIKOM-VASIL DINKOV
========================================
 LEAVE THESE NOTES PLEASE */


// ===
if(s_eS)event=null;s_tmp="";s_T=s_ST=s_hd=s_u=s_d=0;s_N=null;s_O=[""];s_S=[""];s_P=[];s_scrO=s_iE||s_oP||s_nS||s_kN?1:0;s_bT=s_iE||s_kN||s_sF?1:0;s_dE=s_x[s_x.compatMode=="CSS1Compat"?"documentElement":"body"];s_iEf=s_iE&&!s_mC?"height:1%;":"";s_iE5Mf=s_iE5M?";margin-right:-15px;margin-bottom:-15px":"";s_F=s_iE&&!s_mC&&typeof(s_hideSELECTsInIE)!=s_a&&s_hideSELECTsInIE?1:0;s_iF=s_F&&s_x.createEventObject?1:0;s_iA=[""];s_F=s_F&&!s_x.createEventObject?1:0;s_p=s_rightToLeft?"left":"right";s_r="transparent";function s_A(){var i,j,k,q,d,s,m,o;for(i=0;i<s_2.length;i++){if(typeof s_1[s_2[i]]==s_a)continue;q=[];for(j=0;j<s_1[s_2[i]].length;j++)q[j]=s_1[s_2[i]][j];d=0;while(d<q.length){m=s_[q[d][0]][0];o=s_[q[d][0]][q[d][1]];s=m.S;o.SELECTED=true;with(o){if(!s_autoSELECTEDTreeItemsClickable)U="";BgColor=s[24];FontColor=s[25];SubImgSrc=s[26];Class=s[34];OverClass=s[34];BorderColor=s[38]};if(typeof s_1[m.N]!=s_a)for(k=0;k<s_1[m.N].length;k++)q[q.length]=s_1[m.N][k];d++}}};if(s_autoSELECTED&&typeof(s_autoSELECTEDTree)!=s_a&&s_autoSELECTEDTree)s_A();function s_nr(a){for(var i=1;i<=s_ct;i++)if(a==s_[i][0].N)break;return i};function s_getO(a){return s_x.getElementById(a)};function s_getOS(a){return s_x.getElementById(a).style};function s_rl(){if(!s_iE||s_mC)return false;var o=s_x.body;while(o.parentNode){if(o.dir=="rtl"||o.style&&o.style.direction=="rtl")return true;o=o.parentNode};return false};function s_ri(a){var i,t="",o,c,q,e,l,p,b,r,s,g;for(i=1;i<s_[a].length;i++){o=s_[a][i];l=o.U&&o.U!=""&&!o.DISABLED?"a":"div";q=o.Title?" title='"+o.Title+"'":"";c=o.Class||o.OverClass?"<span"+(o.Class?" class='"+o.Class+"'":"")+" id=s_c"+a+"_"+i+">":"";e=o.Class||o.OverClass?"</span>":"";p=o.Padding;b=o.BorderWidth;r=o.BorderColor;s=o.SubImgWidth;g=o.Target;t+="<div id=s_m"+a+"_"+i+" "+(!o.DISABLED?"onmouseover=s_ov(this,"+a+","+i+") onmouseout=if(s_ST)clearTimeout(s_ST);s_ST=0 ":" ")+"style=position:relative><"+l+" href='"+o.U+"'"+q+" target="+(o.U.indexOf("javascript:")==0||g=="self"?"_self":g=="newWindow"?"_blank":g=="top"?"_top":g)+" onfocus=blur() style='display:block;"+s_iEf+"background:"+o.BgColor+";"+(o.BgImage?"background-image:url(\""+o.BgImage+"\");":"")+"line-height:normal"+(l=="div"?";cursor:default":"")+";color:"+o.FontColor+";font-family:"+o.FontFamily+";font-size:"+o.FontSize+";text-decoration:none;border-style:solid;border-width:"+(s_bT&&r==s_r?0:b)+"px;border-color:"+r+";padding:"+(s_bT&&r==s_r?b:0)+"px;text-align:"+o.TextAlign+";font-weight:"+o.FontWeight+"'>"+"<div style='padding:"+p+"px"+(o.Image&&o.T==""&&s_nS?";height:"+o.Image[2]+"px;overflow:hidden":"")+(!s_[a][0].W&&s_kN31p&&!s_kN32p?";white-space:nowrap":"")+(o.Show&&o.Show!=""&&o.UseSubImg?(";padding-"+s_p+":"+(o.Image&&o.T==""?p:p*2+s)+"px'>"):"'>")+c+(o.Image?(o.ImageP=="right"?(s_[a][0].W?"":"<nobr>")+o.T:(s_[a][0].W?"":"<nobr>"))+"<img src='"+o.Image[0]+"' border=0 "+(o.Image[3]?"alt='"+o.Image[3]+"' ":"")+"width="+o.Image[1]+" height="+o.Image[2]+(o.ImageA?" align="+o.ImageA:"")+" id=s_I"+a+"_"+i+">"+(!o.ImageP||o.ImageP=="left"?o.T+(s_[a][0].W?"":"</nobr>"):(s_[a][0].W?"":"</nobr>")):(s_[a][0].W?"":"<nobr>")+o.T+(s_[a][0].W?"":"</nobr>"))+e+"</div>"+"</"+l+">"+(o.Show&&o.Show!=""&&o.UseSubImg?("<img src='"+o.OverSubImgSrc+"' border=0 style='position:absolute;top:"+o.SubImgTop+"px;"+s_p+":"+(p+b)+"px;width:"+s+"px;height:"+o.SubImgHeight+"px' id=s_io"+a+"_"+i+"><img src='"+o.SubImgSrc+"' border=0 style='position:absolute;top:"+o.SubImgTop+"px;"+s_p+":"+(p+b)+"px;background:"+o.BgColor+";width:"+s+"px;height:"+o.SubImgHeight+"px' id=s_i"+a+"_"+i+">"):"")+"</div>"+(i<s_[a].length-1&&o.SeparatorSize>0?"<div style='height:"+o.SeparatorSize+"px;background:"+o.SeparatorColor+";"+(o.SeparatorBgImage?"background-image:url(\""+o.SeparatorBgImage+"\");":"")+"margin:"+(o.SeparatorSpacing)+"px 0"+(!s_iE5M?";overflow:hidden;font-family:sans-serif;font-size:1px":"")+"'></div>":"")}return t};function s_sP(){var c=s_getO("s_m"+s_ct);if(c[s_iE&&!s_mC?"clientWidth":"offsetWidth"]!=parseInt(c.style.width)){setTimeout("s_sP()",50);return}var i,px="px";for(i=0;i<s_P.length;i++){c=s_getOS("s_m"+s_P[i]);c.left=eval(s_[s_P[i]][0].L)+px;c.top=eval(s_[s_P[i]][0].T)+px;c.visibility="visible"}s_ML=true};function s_ov(a,b,c){var A=s_[b][c],s=A.Show&&A.Show!=""?s_nr(A.Show):0;if(s_N==a&&s&&s_subShowTimeout>100&&!s_ST)s_ST=setTimeout("s_as("+s+");s_ss("+s+","+b+","+c+")",s_subShowTimeout);if(s_N==a)return;s_N=a;var k,i,l=s_[b][0].LV,z=0,r=A.BorderColor,f=A.OverBorderColor,w=A.BorderWidth;if(s_S.length>l+1&&s_S[l+1].style.visibility.toLowerCase()!="hidden"){k=s_O[l][0]!=a?l:l+1;for(i=s_S.length-1;i>k;i--){s_S[i].style.visibility="hidden";z=s_S[i].id.substring(3);if(s_S[i].SC)s_oi(z);if(s_iF)s_hi(z)}}if(l==1&&s_S[1]&&eval(s_S[1].id.substring(3))!=b&&!s_[s_S[1].id.substring(3)][0].P){s_S[1].style.visibility="hidden";z=s_S[1].id.substring(3);if(s_S[1].SC)s_oi(z);if(s_iF)s_hi(z)}if(!s_keepHighlighted&&s_O[l-1]&&s_O[l-1][0].on)s_cB(l-1);if(s_O[l+1]&&s_O[l+1][0].on)s_cB(l+1);if(s_O[l]&&s_O[l][0].on)s_cB(l);s_O[l]=[a,b,c];if(!s_S[l]||s_S[l].id!="s_m"+b)s_S[l]=s_getO("s_m"+b);if(!A.NOROLL&&!A.SELECTED){with(a.firstChild.style){if(w>0){if(s_bT&&f==s_r&&r!=s_r){borderWidth="0px";padding=w+"px"}else{if(s_bT&&r==s_r&&f!=s_r){borderWidth=w+"px";padding="0px"}borderColor=f}}background=A.OverBgColor;color=A.OverFontColor;if(A.OnBgImage)backgroundImage="url("+A.OnBgImage+")";else if(A.BgImage&&(!s_nS||s_sF))backgroundImage=""}if(A.OverClass){s_getO("s_c"+b+"_"+c).className=A.OverClass;s_O[l][0].c=1}if(A.Show&&A.Show!=""&&A.UseSubImg)s_getOS("s_i"+b+"_"+c).visibility="hidden";if(A.Image&&A.OnImage)s_getO("s_I"+b+"_"+c).src=A.OnImage;s_O[l][0].on=1}if(!s)return;if(s_subShowTimeout>100){s_ST=setTimeout("s_as("+s+");s_ss("+s+","+b+","+c+")",s_subShowTimeout)}else{s_as(s);s_ss(s,b,c)}};function s_as(a){var r,rs,M;r=s_getO("s_m"+a);rs=r.style;M=s_[a][0];if(rs.visibility.toLowerCase()=="visible")return;s_f=s_nS&&!s_x.body.clientWidth?15:0;s_h=s_iE?s_dE.clientHeight:s_nS&&s_x.body.clientHeight&&!s_sF?s_dE.clientHeight?s_dE.clientHeight:s_x.body.clientHeight:s_oP?s_x.body.clientHeight:innerHeight-s_f;s_w=s_iE?s_dE.clientWidth:s_nS&&s_x.body.clientWidth&&!s_sF?s_dE.clientWidth?s_dE.clientWidth:s_x.body.clientWidth:s_oP?s_x.body.clientWidth:innerWidth-s_f;s_t=s_iE?s_dE.scrollTop:pageYOffset;s_l=s_iE?s_dE.scrollLeft-(s_rl()?s_dE.scrollWidth-s_w:0):pageXOffset;if(!M.SC||!s_scrO)return;var sc,ss,h=s_h-M.BW*2-M.PD*2;sc=s_getOS("s_m"+a+"_sc");ss=s_getO("s_m"+a+"_ss");if(r.offsetHeight>s_h||r.SC)if(ss.offsetHeight>h){if(r.SC){if(parseInt(sc.height)==h-M.SCH*2)return;if(parseInt(sc.height)<h-M.SCH*2)ss.style.top=(parseInt(ss.style.top)+h-M.SCH*2-parseInt(sc.height)<0?parseInt(ss.style.top)+h-M.SCH*2-parseInt(sc.height):0)+"px"}sc.height=h-M.SCH*2+"px";ss.h=parseInt(sc.height);r.SC=1}else{sc.height=ss.offsetHeight+"px";ss.style.top="0px";r.SC=0}};function s_ss(a,b,c){var r,rs;r=s_getO("s_m"+a);rs=r.style;if(rs.visibility.toLowerCase()=="visible")return;var P,L,T,H,W,M,S,o,px,m,w,t,l,s,defaultY,defaultX;px="px";H=r.offsetHeight?r.offsetHeight:rs.pixelHeight;W=r.offsetWidth?r.offsetWidth:rs.pixelWidth;M=s_[b][0];s=s_[b][c];S=s_[a][0];o=s_getO("s_m"+b+"_"+c);m=s_getO("s_m"+b);w=s_iE&&!s_mC?m.clientWidth:m.offsetWidth?m.offsetWidth:m.style.pixelWidth;s_S[S.LV]=r;if(s_F)s_htg();if(s_iF&&!S.iF)s_if(a);P=o.offsetParent;L=o.offsetLeft;T=o.offsetTop;while(P&&P.tagName.toLowerCase()!="body"){L+=P.style.pixelLeft?P.style.pixelLeft:P.offsetLeft;T+=P.style.pixelTop?P.style.pixelTop:P.offsetTop;P=P.offsetParent}defaultY=T+s_subMenuOffsetY;defaultX=s_rightToLeft?L-W+s_subMenuOffsetX:L+w-M.BW*2-M.PD*2-s_subMenuOffsetX;t=typeof(S.T)!="number"&&S.T==""?defaultY:eval(S.T);l=typeof(S.L)!="number"&&S.L==""?defaultX:eval(S.L);if(r.SC)rs.top=s_t+S.SCH+px;else if(S.SC&&!s_scrO&&H>s_h)rs.top=s_t+px;else rs.top=(t+H>s_t+s_h)?s_t+s_h-H+px:(t<s_t)?(s_t+H>s_t+s_h)?s_t+s_h-H+px:s_t+px:t+px;if(typeof S.L!="number"&&(S.L==""||S.L.indexOf("defaultX")!=-1)){if(s_rightToLeft)rs.left=(l>=s_l)?l+px:L+w-M.BW*2-M.PD*2-s_subMenuOffsetX+defaultX-l+px;else rs.left=(l+W>s_l+s_w)?L-W+s_subMenuOffsetX+defaultX-l+px:l+px}else{if(s_rightToLeft)rs.left=(l>=s_l)?l+px:s_l+px;else rs.left=(l+W>s_l+s_w)?s_l+s_w-W+px:l+px}if(r.SC){var u,d;u=s_getOS("s_m"+a+"_u");d=s_getOS("s_m"+a+"_d");u.zIndex=rs.zIndex;d.zIndex=u.zIndex;u.top=s_t+px;d.top=s_t+s_h-S.SCH+px;u.left=parseInt(rs.left)+parseInt((W-S.SCW)/2)+px;d.left=u.left}if(s_iF)s_si(a,r);if(r.filters&&r.filters.length!=0)s_sh2(r);else rs.visibility="visible"};function s_oi(a){var u,d;u=s_getOS("s_m"+a+"_u");d=s_getOS("s_m"+a+"_d");u.top="-1000px";d.top="-1000px"};function s_iEW(a){if(!s_iE)return;if(event.wheelDelta==120)s_U(a,30);else s_D(a,30)};function s_U(a,b){var o,os;o=s_getO("s_m"+a+"_ss");os=o.style;if(os.top=="0px"){if(s_u){clearInterval(s_u);s_u=0}return}if(parseInt(os.top)>-b)os.top="0px";else os.top=parseInt(os.top)+b+"px"};function s_D(a,b){var o,os;o=s_getO("s_m"+a+"_ss");os=o.style;if(os.top==o.h-o.offsetHeight+"px"){if(s_d){clearInterval(s_d);s_d=0}return}if(o.h-o.offsetHeight-parseInt(os.top)>-b)os.top=o.h-o.offsetHeight+"px";else os.top=parseInt(os.top)-b+"px"};function s_hide(){if(!s_ML)return;if(!s_kN)clearTimeout(s_T);s_T=setTimeout("s_hide2()",s_hideTimeout)};function s_hide2(){if(s_ST){clearTimeout(s_ST);s_ST=0};for(var i=s_S.length-1;i>0;i--){if(i!=1||!s_[s_S[1].id.substring(3)][0].P)s_S[i].style.visibility="hidden";if(s_O[i]&&s_O[i][0].on)s_cB(i);if(s_S[i].SC)s_oi(s_S[i].id.substring(3));if(s_iF)s_hi(s_S[i].id.substring(3))}s_S=[""];s_O=[""];s_N=null;if(s_F)s_stg()};function s_cB(a){var A=s_[s_O[a][1]][s_O[a][2]];var r=A.BorderColor,f=A.OverBorderColor,w=A.BorderWidth?A.BorderWidth:0;with(s_O[a][0].firstChild.style){if(w>0){if(s_bT&&r==s_r&&f!=s_r){borderWidth="0px";padding=w+"px"}else{if(s_bT&&f==s_r&&r!=s_r){borderWidth=w+"px";padding="0px"}borderColor=r}}background=A.BgColor;color=A.FontColor;if(A.BgImage)backgroundImage="url("+A.BgImage+")";else if(A.OnBgImage&&(!s_nS||s_sF))backgroundImage=""}if(s_O[a][0].c)s_getO("s_c"+s_O[a][0].id.substring(3)).className=A.Class?A.Class:"";if(A.Show&&A.Show!=""&&A.UseSubImg)s_getOS("s_i"+s_O[a][0].id.substring(3)).visibility="inherit";if(A.Image&&A.OnImage)s_getO("s_I"+s_O[a][0].id.substring(3)).src=A.Image[0];s_O[a][0].on=0};function s_show(a,e){if((s_Any_Add_On_Source!=""&&!s_AL)||!s_ML)return;var n,M;n=s_nr(a);M=s_[n][0];if(M.LV!=1){alert("ERROR:\nYou are calling the '"+a+"' menu, which is not a first level menu!\nThe s_show() function can only show menus with LV:1 set.");return}if(M.P){alert("ERROR:\nYou are calling the '"+a+"' menu, which is a permanent menu!");return}clearTimeout(s_T);if(s_ST){clearTimeout(s_ST);s_ST=0}s_as(n);var r,rs;r=s_getO("s_m"+n);rs=r.style;if(rs.visibility.toLowerCase()=="visible")return;var mouseX,mouseY,cL,cT,H,W,px;s_hide2();if(s_F)s_htg();if(s_iF&&!M.iF)s_if(n);s_S[1]=r;e=event?event:e;mouseX=s_iE||s_oP||s_kN31p?e.clientX+s_l-(s_rl()?s_dE.offsetWidth-s_w:0):s_oP||s_kN?e.clientX:e.pageX;mouseY=s_iE||s_oP||s_kN31p?e.clientY+s_t:s_oP||s_kN?e.clientY:e.pageY;cL=typeof(M.L)!="number"&&M.L==""?mouseX:eval(M.L);cT=typeof(M.T)!="number"&&M.T==""?mouseY:eval(M.T);H=r.offsetHeight?r.offsetHeight:rs.pixelHeight;W=r.offsetWidth?r.offsetWidth:rs.pixelWidth;px="px";if(r.SC)rs.top=s_t+M.SCH+px;else if(M.SC&&!s_scrO&&H>s_h)rs.top=s_t+px;else rs.top=(cT+H>s_t+s_h)?s_t+s_h-H+px:cT+px;rs.left=(cL+W>s_l+s_w)?s_l+s_w-W+px:cL+px;if(r.SC){var u,d;u=s_getOS("s_m"+n+"_u");d=s_getOS("s_m"+n+"_d");u.zIndex=rs.zIndex;d.zIndex=u.zIndex;u.top=s_t+px;d.top=s_t+s_h-M.SCH+px;u.left=parseInt(rs.left)+parseInt((W-M.SCW)/2)+px;d.left=u.left}if(s_iF)s_si(n,r);if(r.filters&&r.filters.length!=0)s_sh2(r);else rs.visibility=s_nS&&!s_sF?setTimeout("s_sh()",1):"visible"};function s_sh(){s_S[1].style.visibility="visible"};function s_sh2(a){if(typeof a.filters[0].apply!=s_a)a.filters[0].apply();a.style.visibility="visible";if(typeof a.filters[0].play!=s_a)a.filters[0].play()};function s_htg(){if(s_hd)return;var i,l;l=s_x.all.tags("SELECT");for(i=0;i<l.length;i++){l[i].vis=l[i].style.visibility;l[i].style.visibility="hidden"}s_hd=1};function s_stg(){if(!s_hd)return;var i,l;l=s_x.all.tags("SELECT");for(i=0;i<l.length;i++)l[i].style.visibility=l[i].vis;s_hd=0};function s_if(a){s_iA[a]=s_x.createElement("iframe");s_iA[a].src="javascript:false";with(s_iA[a].style){position="absolute";zIndex=2009;filter="alpha(opacity=0)";visibility="hidden";width="1px";height="1px"}s_x.body.appendChild(s_iA[a]);s_[a][0].iF=1};function s_si(a,o){if(!s_iA[a])return;with(s_iA[a].style){width=o.offsetWidth+"px";height=o.offsetHeight+"px";top=o.style.top;left=o.style.left;visibility="visible"}};function s_hi(a){if(!s_iA[a])return;s_iA[a].style.visibility="hidden"};function s_hf(a){var m,j,c,s,W,l,S,px="px",S=s_[a][0];m=s_getOS("s_m"+a);c=s_getOS("s_m"+a+"_sc");s=s_getO("s_m"+a+"_ss");W=S.W?parseInt(s.style.width):s_oP?s.firstChild.offsetWidth:s.offsetWidth?s.offsetWidth:s.style.pixelWidth;if(s_kN||s_iE5M||s_eS)c.height=s.offsetHeight+"px";c.width=W+"px";m.width=W+S.BW*2+S.PD*2+"px"};function s_hf2(){for(var i=1;i<=s_ct;i++)s_hf(i);s_sP()};function s_ld(){var m=s_getO("s_m"+s_ct),s=s_getO("s_m"+s_ct+"_ss"),W=0;if(m&&s){W=s_oP?s_[s_ct][0].W?s.offsetWidth:s.firstChild.offsetWidth:s.offsetWidth?s.offsetWidth:1;if(W>0){s_hf2();return}}setTimeout("s_ld()",50)};for(s_j=1;s_j<=s_ct;s_j++){
s_tmp+="<div style='position:absolute;top:-3000px;left:0px;direction:ltr;z-index:"+(s_[s_j][0].P?2010:2010+s_[s_j][0].LV*2)+";"+(s_[s_j][0].BGI?"background-image:url(\""+s_[s_j][0].BGI+"\");":"")+"visibility:hidden"+s_iE5Mf+(s_[s_j][0].W?(";width:"+s_[s_j][0].W+"px"):s_kN&&!s_kN31p?";width:100%":"")+(s_iE&&!s_mC&&s_[s_j][0].IEF!=""?";filter:"+s_[s_j][0].IEF:"")+"' id=s_m"+s_j+" onmouseover=clearTimeout(s_T) onmouseout=s_hide() onselectstart='return false' onmousewheel='if(this.SC){s_iEW("+s_j+");return false}'>"+(s_iE&&!s_mC?"<table cellpadding=0 cellspacing=0 border=0 style=padding:0px><tr><td>":"")+"<div style='"+s_iEf+(s_[s_j][0].BC!=s_r?"border-style:solid;border-color:"+s_[s_j][0].BC+";border-width":"padding")+":"+s_[s_j][0].BW+"px'><div style='"+s_iEf+(s_[s_j][0].B!=s_r&&!s_[s_j][0].BGI?"border-style:solid;border-color:"+s_[s_j][0].B+";border-width":"padding")+":"+s_[s_j][0].PD+"px;background:"+(s_[s_j][0].BGI?s_r:s_[s_j][0].B)+"'><div style="+(s_scrO?"overflow:hidden;":"")+"position:relative"+(s_[s_j][0].W?(";width:"+(s_[s_j][0].W-s_[s_j][0].BW*2-s_[s_j][0].PD*2)+"px"):(s_iE&&!s_mC?";width:1px":""))+" id=s_m"+s_j+"_sc><div style=position:"+(s_kN||s_iE5M||s_eS?"absolute":"relative")+";top:0px"+s_iE5Mf+";left:0px"+(s_[s_j][0].W?(";width:"+(s_[s_j][0].W-s_[s_j][0].BW*2-s_[s_j][0].PD*2)+"px"):(s_iE&&!s_mC?";width:1px":""))+" id=s_m"+s_j+"_ss>"+(s_[s_j][0].W?"":"<table cellpadding=0 cellspacing=0 border=0 style=padding:0px"+(s_[s_j][0].MinW?";width:"+(s_[s_j][0].MinW-s_[s_j][0].BW*2-s_[s_j][0].PD*2)+"px;overflow:visible":"")+"><tr><td>")+s_ri(s_j)+(s_[s_j][0].W?"":"</td></tr></table>")+"</div></div></div></div>"+(s_iE&&!s_mC?"</td></tr></table>":"")+"</div>"+(s_[s_j][0].SC&&s_scrO?"<img src='"+s_[s_j][0].SCT+"' width="+s_[s_j][0].SCW+" height="+s_[s_j][0].SCH+" style=position:absolute;top:-1000px;left:0px;z-index:2010 id=s_m"+s_j+"_u onmouseover=clearTimeout(s_T);if(!s_u)s_u=setInterval('s_U("+s_j+",10)',"+s_scrollingInterval+") onmouseout=s_hide();if(s_u){clearInterval(s_u);s_u=0}><img src='"+s_[s_j][0].SCB+"' width="+s_[s_j][0].SCW+" height="+s_[s_j][0].SCH+" style=position:absolute;top:-1000px;left:0px;z-index:2010 id=s_m"+s_j+"_d onmouseover=clearTimeout(s_T);if(!s_d)s_d=setInterval('s_D("+s_j+",10)',"+s_scrollingInterval+") onmouseout=s_hide();if(s_d){clearInterval(s_d);s_d=0}>":"");if(s_[s_j][0].LV==1&&s_[s_j][0].P)s_P[s_P.length]=s_j}s_x.write(s_tmp);s_ld();if(s_P.length!=0){s_dO=window.onresize?window.onresize:function(){};window.onresize=function(){s_iE5M?setTimeout("s_sP()",500):s_sP();s_dO()}}