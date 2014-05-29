
#include "stdafx.h"

// MSG Array LOOKUP
const int MAX_MSG_LOOKUP = 1024;
const int MAX_MSG_NAME = 21;
TCHAR MSG_LOOKUP[MAX_MSG_LOOKUP][MAX_MSG_NAME] = {
};


//void InitializeMsgLookup() below
void InitializeMsgLookup(int allowList[], int allowSize) 
{
	for (int i = 0 ; i < MAX_MSG_LOOKUP ; i++)
	{
		bool allowFlg = true;
		if (allowSize > 0)
			allowFlg = false;
		for (int a = 0 ; a < allowSize ; a++)
			if (allowList[a] == i)
				allowFlg = true;
		if (!allowFlg)
		{
			memset((void *)&MSG_LOOKUP[i], '\0', sizeof(TCHAR) * MAX_MSG_NAME); //blank it
			continue;
		}
		switch (i)
		{
			case WM_NULL:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NULL")); break;
			case WM_CREATE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CREATE")); break;
			case WM_DESTROY:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DESTROY")); break;
			case WM_MOVE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MOVE")); break;
			case WM_SIZE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SIZE")); break;
			case WM_ACTIVATE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_ACTIVATE")); break;
			case WM_SETFOCUS:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SETFOCUS")); break;
			case WM_KILLFOCUS:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_KILLFOCUS")); break;
			case WM_ENABLE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_ENABLE")); break;
			case WM_SETREDRAW:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SETREDRAW")); break;
			case WM_SETTEXT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SETTEXT")); break;
			case WM_GETTEXT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_GETTEXT")); break;
			case WM_GETTEXTLENGTH:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_GETTEXTLENGTH")); break;
			case WM_PAINT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PAINT")); break;
			case WM_CLOSE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CLOSE")); break;
			case WM_QUERYENDSESSION:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_QUERYENDSESSION")); break;
			case WM_QUIT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_QUIT")); break;
			case WM_QUERYOPEN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_QUERYOPEN")); break;
			case WM_ERASEBKGND:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_ERASEBKGND")); break;
			case WM_SYSCOLORCHANGE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SYSCOLORCHANGE")); break;
			case WM_ENDSESSION:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_ENDSESSION")); break;
			case 0x17:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SYSTEMERROR")); break;
			case WM_SHOWWINDOW:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SHOWWINDOW")); break;
			case 0x19:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CTLCOLOR")); break;
			case WM_WININICHANGE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_WININICHANGE")); break;
			//case WM_SETTINGCHANGE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SETTINGCHANGE")); break;
			case WM_DEVMODECHANGE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DEVMODECHANGE")); break;
			case WM_ACTIVATEAPP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_ACTIVATEAPP")); break;
			case WM_FONTCHANGE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_FONTCHANGE")); break;
			case WM_TIMECHANGE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_TIMECHANGE")); break;
			case WM_CANCELMODE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CANCELMODE")); break;
			case WM_SETCURSOR:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SETCURSOR")); break;
			case WM_MOUSEACTIVATE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MOUSEACTIVATE")); break;
			case WM_CHILDACTIVATE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CHILDACTIVATE")); break;
			case WM_QUEUESYNC:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_QUEUESYNC")); break;
			case WM_GETMINMAXINFO:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_GETMINMAXINFO")); break;
			case WM_PAINTICON:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PAINTICON")); break;
			case WM_ICONERASEBKGND:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_ICONERASEBKGND")); break;
			case WM_NEXTDLGCTL:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NEXTDLGCTL")); break;
			case WM_SPOOLERSTATUS:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SPOOLERSTATUS")); break;
			case WM_DRAWITEM:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DRAWITEM")); break;
			case WM_MEASUREITEM:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MEASUREITEM")); break;
			case WM_DELETEITEM:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DELETEITEM")); break;
			case WM_VKEYTOITEM:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_VKEYTOITEM")); break;
			case WM_CHARTOITEM:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CHARTOITEM")); break;
			case WM_SETFONT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SETFONT")); break;
			case WM_GETFONT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_GETFONT")); break;
			case WM_SETHOTKEY:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SETHOTKEY")); break;
			case WM_GETHOTKEY:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_GETHOTKEY")); break;
			case WM_QUERYDRAGICON:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_QUERYDRAGICON")); break;
			case WM_COMPAREITEM:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_COMPAREITEM")); break;
			case WM_COMPACTING:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_COMPACTING")); break;
			case WM_WINDOWPOSCHANGING:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_WINDOWPOSCHANGING")); break;
			case WM_WINDOWPOSCHANGED:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_WINDOWPOSCHANGED")); break;
			case WM_POWER:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_POWER")); break;
			case WM_COPYDATA:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_COPYDATA")); break;
			case WM_CANCELJOURNAL:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CANCELJOURNAL")); break;
			case WM_NOTIFY:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NOTIFY")); break;
			case WM_INPUTLANGCHANGEREQUEST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_INPUTLANGCHANGERE")); break;
			case WM_INPUTLANGCHANGE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_INPUTLANGCHANGE")); break;
			case WM_TCARD:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_TCARD")); break;
			case WM_HELP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_HELP")); break;
			case WM_USERCHANGED:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_USERCHANGED")); break;
			case WM_NOTIFYFORMAT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NOTIFYFORMAT")); break;
			case WM_CONTEXTMENU:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CONTEXTMENU")); break;
			case WM_STYLECHANGING:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_STYLECHANGING")); break;
			case WM_STYLECHANGED:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_STYLECHANGED")); break;
			case WM_DISPLAYCHANGE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DISPLAYCHANGE")); break;
			case WM_GETICON:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_GETICON")); break;
			case WM_SETICON:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SETICON")); break;
			case WM_NCCREATE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCCREATE")); break;
			case WM_NCDESTROY:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCDESTROY")); break;
			case WM_NCCALCSIZE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCCALCSIZE")); break;
			case WM_NCHITTEST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCHITTEST")); break;
			case WM_NCPAINT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCPAINT")); break;
			case WM_NCACTIVATE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCACTIVATE")); break;
			case WM_GETDLGCODE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_GETDLGCODE")); break;
			case WM_NCMOUSEMOVE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCMOUSEMOVE")); break;
			case WM_NCLBUTTONDOWN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCLBUTTONDOWN")); break;
			case WM_NCLBUTTONUP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCLBUTTONUP")); break;
			case WM_NCLBUTTONDBLCLK:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCLBUTTONDBLCLK")); break;
			case WM_NCRBUTTONDOWN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCRBUTTONDOWN")); break;
			case WM_NCRBUTTONUP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCRBUTTONUP")); break;
			case WM_NCRBUTTONDBLCLK:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCRBUTTONDBLCLK")); break;
			case WM_NCMBUTTONDOWN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCMBUTTONDOWN")); break;
			case WM_NCMBUTTONUP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCMBUTTONUP")); break;
			case WM_NCMBUTTONDBLCLK:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCMBUTTONDBLCLK")); break;
			//case WM_KEYFIRST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_KEYFIRST")); break;
			case WM_KEYDOWN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_KEYDOWN")); break;
			case WM_KEYUP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_KEYUP")); break;
			case WM_CHAR:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CHAR")); break;
			case WM_DEADCHAR:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DEADCHAR")); break;
			case WM_SYSKEYDOWN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SYSKEYDOWN")); break;
			case WM_SYSKEYUP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SYSKEYUP")); break;
			case WM_SYSCHAR:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SYSCHAR")); break;
			case WM_SYSDEADCHAR:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SYSDEADCHAR")); break;
			case WM_KEYLAST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_KEYLAST")); break;
			case WM_IME_STARTCOMPOSITION:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_STARTCOMPOSIT")); break;
			case WM_IME_ENDCOMPOSITION:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_ENDCOMPOSITIO")); break;
			case WM_IME_COMPOSITION:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_COMPOSITION")); break;
			//case WM_IME_KEYLAST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_KEYLAST")); break;
			case WM_INITDIALOG:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_INITDIALOG")); break;
			case WM_COMMAND:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_COMMAND")); break;
			case WM_SYSCOMMAND:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SYSCOMMAND")); break;
			case WM_TIMER:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_TIMER")); break;
			case WM_HSCROLL:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_HSCROLL")); break;
			case WM_VSCROLL:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_VSCROLL")); break;
			case WM_INITMENU:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_INITMENU")); break;
			case WM_INITMENUPOPUP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_INITMENUPOPUP")); break;
			case WM_MENUSELECT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MENUSELECT")); break;
			case WM_MENUCHAR:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MENUCHAR")); break;
			case WM_ENTERIDLE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_ENTERIDLE")); break;
			case WM_CTLCOLORMSGBOX:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CTLCOLORMSGBOX")); break;
			case WM_CTLCOLOREDIT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CTLCOLOREDIT")); break;
			case WM_CTLCOLORLISTBOX:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CTLCOLORLISTBOX")); break;
			case WM_CTLCOLORBTN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CTLCOLORBTN")); break;
			case WM_CTLCOLORDLG:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CTLCOLORDLG")); break;
			case WM_CTLCOLORSCROLLBAR:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CTLCOLORSCROLLBAR")); break;
			case WM_CTLCOLORSTATIC:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CTLCOLORSTATIC")); break;
			//case WM_MOUSEFIRST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MOUSEFIRST")); break;
			case WM_MOUSEMOVE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MOUSEMOVE")); break;
			case WM_LBUTTONDOWN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_LBUTTONDOWN")); break;
			case WM_LBUTTONUP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_LBUTTONUP")); break;
			case WM_LBUTTONDBLCLK:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_LBUTTONDBLCLK")); break;
			case WM_RBUTTONDOWN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_RBUTTONDOWN")); break;
			case WM_RBUTTONUP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_RBUTTONUP")); break;
			case WM_RBUTTONDBLCLK:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_RBUTTONDBLCLK")); break;
			case WM_MBUTTONDOWN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MBUTTONDOWN")); break;
			case WM_MBUTTONUP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MBUTTONUP")); break;
			case WM_MBUTTONDBLCLK:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MBUTTONDBLCLK")); break;
			case WM_MOUSEWHEEL:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MOUSEWHEEL")); break;
			case WM_MOUSEHWHEEL:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MOUSEHWHEEL")); break;
			case WM_PARENTNOTIFY:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PARENTNOTIFY")); break;
			case WM_ENTERMENULOOP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_ENTERMENULOOP")); break;
			case WM_EXITMENULOOP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_EXITMENULOOP")); break;
			case WM_NEXTMENU:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NEXTMENU")); break;
			case WM_SIZING:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SIZING")); break;
			case WM_CAPTURECHANGED:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CAPTURECHANGED")); break;
			case WM_MOVING:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MOVING")); break;
			case WM_POWERBROADCAST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_POWERBROADCAST")); break;
			case WM_DEVICECHANGE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DEVICECHANGE")); break;
			case WM_MDICREATE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDICREATE")); break;
			case WM_MDIDESTROY:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDIDESTROY")); break;
			case WM_MDIACTIVATE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDIACTIVATE")); break;
			case WM_MDIRESTORE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDIRESTORE")); break;
			case WM_MDINEXT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDINEXT")); break;
			case WM_MDIMAXIMIZE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDIMAXIMIZE")); break;
			case WM_MDITILE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDITILE")); break;
			case WM_MDICASCADE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDICASCADE")); break;
			case WM_MDIICONARRANGE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDIICONARRANGE")); break;
			case WM_MDIGETACTIVE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDIGETACTIVE")); break;
			case WM_MDISETMENU:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDISETMENU")); break;
			case WM_ENTERSIZEMOVE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_ENTERSIZEMOVE")); break;
			case WM_EXITSIZEMOVE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_EXITSIZEMOVE")); break;
			case WM_DROPFILES:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DROPFILES")); break;
			case WM_MDIREFRESHMENU:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MDIREFRESHMENU")); break;
			case WM_IME_SETCONTEXT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_SETCONTEXT")); break;
			case WM_IME_NOTIFY:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_NOTIFY")); break;
			case WM_IME_CONTROL:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_CONTROL")); break;
			case WM_IME_COMPOSITIONFULL:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_COMPOSITIONFU")); break;
			case WM_IME_SELECT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_SELECT")); break;
			case WM_IME_CHAR:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_CHAR")); break;
			case WM_IME_KEYDOWN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_KEYDOWN")); break;
			case WM_IME_KEYUP:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_IME_KEYUP")); break;
			case WM_MOUSEHOVER:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MOUSEHOVER")); break;
			case WM_NCMOUSELEAVE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_NCMOUSELEAVE")); break;
			case WM_MOUSELEAVE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_MOUSELEAVE")); break;
			case WM_CUT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CUT")); break;
			case WM_COPY:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_COPY")); break;
			case WM_PASTE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PASTE")); break;
			case WM_CLEAR:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CLEAR")); break;
			case WM_UNDO:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_UNDO")); break;
			case WM_RENDERFORMAT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_RENDERFORMAT")); break;
			case WM_RENDERALLFORMATS:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_RENDERALLFORMATS")); break;
			case WM_DESTROYCLIPBOARD:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DESTROYCLIPBOARD")); break;
			case WM_DRAWCLIPBOARD:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DRAWCLIPBOARD")); break;
			case WM_PAINTCLIPBOARD:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PAINTCLIPBOARD")); break;
			case WM_VSCROLLCLIPBOARD:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_VSCROLLCLIPBOARD")); break;
			case WM_SIZECLIPBOARD:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_SIZECLIPBOARD")); break;
			case WM_ASKCBFORMATNAME:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_ASKCBFORMATNAME")); break;
			case WM_CHANGECBCHAIN:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_CHANGECBCHAIN")); break;
			case WM_HSCROLLCLIPBOARD:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_HSCROLLCLIPBOARD")); break;
			case WM_QUERYNEWPALETTE:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_QUERYNEWPALETTE")); break;
			case WM_PALETTEISCHANGING:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PALETTEISCHANGING")); break;
			case WM_PALETTECHANGED:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PALETTECHANGED")); break;
			case WM_HOTKEY:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_HOTKEY")); break;
			case WM_PRINT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PRINT")); break;
			case WM_PRINTCLIENT:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PRINTCLIENT")); break;
			case WM_HANDHELDFIRST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_HANDHELDFIRST")); break;
			case WM_HANDHELDLAST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_HANDHELDLAST")); break;
			case WM_PENWINFIRST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PENWINFIRST")); break;
			case WM_PENWINLAST:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_PENWINLAST")); break;
			case 0x390:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_COALESCE_FIRST")); break;
			case 0x39F:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_COALESCE_LAST")); break;
			case 0x3E0:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_FIRST")); break;
			//case 0x3E0:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_INITIATE")); break;
			case 0x3E1:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_TERMINATE")); break;
			case 0x3E2:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_ADVISE")); break;
			case 0x3E3:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_UNADVISE")); break;
			case 0x3E4:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_ACK")); break;
			case 0x3E5:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_DATA")); break;
			case 0x3E6:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_REQUEST")); break;
			case 0x3E7:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_POKE")); break;
			case 0x3E8:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_EXECUTE")); break;
			//case 0x3E8:	_tcscpy_s(MSG_LOOKUP[i], _T("WM_DDE_LAST")); break;

			//case :	_tcscpy_s(MSG_LOOKUP[i], _T("")); break;
			default:
				memset((void *)&MSG_LOOKUP[i], '\0', sizeof(TCHAR) * MAX_MSG_NAME);
				//_tcscpy_s(MSG_LOOKUP[i], 20, _T(""));
			break;
		}
	}
}

void InitializeMsgLookup() 
{
	int allowList[1];
	allowList[0] = -1;
	InitializeMsgLookup(allowList, 0);
}

