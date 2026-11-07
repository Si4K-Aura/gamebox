#define COBJMACROS
#include <stdio.h>
#include <windows.h>
#include <d3d12.h>
typedef HRESULT (WINAPI *PFN_CREATE)(IUnknown*, D3D_FEATURE_LEVEL, REFIID, void**);
int main(void){
  HMODULE m=LoadLibraryA("d3d12.dll");
  if(!m){printf("no d3d12.dll\n");return 1;}
  PFN_CREATE create=(PFN_CREATE)GetProcAddress(m,"D3D12CreateDevice");
  ID3D12Device *dev=NULL;
  HRESULT hr=create(NULL,D3D_FEATURE_LEVEL_11_0,&IID_ID3D12Device,(void**)&dev);
  printf("D3D12CreateDevice hr=0x%08lx\n",(unsigned long)hr);
  if(FAILED(hr)) return 1;
  D3D_FEATURE_LEVEL lv[]={D3D_FEATURE_LEVEL_11_0,D3D_FEATURE_LEVEL_11_1,D3D_FEATURE_LEVEL_12_0,D3D_FEATURE_LEVEL_12_1};
  D3D12_FEATURE_DATA_FEATURE_LEVELS fl={4,lv,0};
  hr=ID3D12Device_CheckFeatureSupport(dev,D3D12_FEATURE_FEATURE_LEVELS,&fl,sizeof(fl));
  printf("max feature level=0x%x hr=0x%08lx\n",(unsigned)fl.MaxSupportedFeatureLevel,(unsigned long)hr);
  return 0;
}
