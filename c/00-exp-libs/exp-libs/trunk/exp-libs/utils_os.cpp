/*****************************************
  Description: ϵͳ���߰�
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#define DLL_IMPLEMENT

#include "stdafx.h"
#include "utils_os.h"
#include <Windows.h>

namespace OS_UTILS {

	DLL_API bool isX64() {
		bool x64 = false;
		typedef VOID (WINAPI *LPFN_GetNativeSystemInfo) (__out LPSYSTEM_INFO lpSystemInfo);
		LPFN_GetNativeSystemInfo fnGetNativeSystemInfo = 
			(LPFN_GetNativeSystemInfo) GetProcAddress(GetModuleHandleW(L"kernel32"), "GetNativeSystemInfo");
		if(fnGetNativeSystemInfo) {
			SYSTEM_INFO sysInfo= { 0 };
			fnGetNativeSystemInfo(&sysInfo);
			if(sysInfo.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_IA64 || 
				sysInfo.wProcessorArchitecture == PROCESSOR_ARCHITECTURE_AMD64) {
					x64 = true;
			}
		}
		return x64;
	}

}