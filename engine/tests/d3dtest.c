#define COBJMACROS
#include <stdio.h>
#include <d3d11.h>
#include <dxgi.h>
int main(void){
  ID3D11Device *dev=NULL; ID3D11DeviceContext *ctx=NULL;
  D3D_FEATURE_LEVEL fl=0;
  HRESULT hr=D3D11CreateDevice(NULL,D3D_DRIVER_TYPE_HARDWARE,NULL,0,NULL,0,D3D11_SDK_VERSION,&dev,&fl,&ctx);
  printf("hr=0x%08lx feature_level=0x%x\n",(unsigned long)hr,(unsigned)fl);
  if(FAILED(hr)) return 1;
  IDXGIDevice *dd=NULL; IDXGIAdapter *ad=NULL; DXGI_ADAPTER_DESC desc;
  ID3D11Device_QueryInterface(dev,&IID_IDXGIDevice,(void**)&dd);
  IDXGIDevice_GetAdapter(dd,&ad);
  IDXGIAdapter_GetDesc(ad,&desc);
  wprintf(L"Adapter: %ls\n",desc.Description);
  return 0;
}
