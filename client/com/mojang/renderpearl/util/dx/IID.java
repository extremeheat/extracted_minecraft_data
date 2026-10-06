package com.mojang.renderpearl.util.dx;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteOrder;

public record IID(long first8Bytes, long second8Bytes) {
   public static final IID IDXGIObject = new IID(-1363005512, 30451, 17977, 155, 224, 40, 235, 67, 166, 122, 46);
   public static final IID IDXGIDeviceSubObject = new IID(1027474297, 63966, 19800, 187, 108, 24, 214, 41, 146, 241, 166);
   public static final IID IDXGIResource = new IID(56572596, 18478, 20048, 180, 31, 138, 127, 139, 216, 150, 11);
   public static final IID IDXGIKeyedMutex = new IID(-1651633527, 55219, 18015, 129, 38, 37, 14, 52, 154, 248, 93);
   public static final IID IDXGISurface = new IID(-889408148, 27331, 18569, 191, 71, 158, 35, 187, 210, 96, 236);
   public static final IID IDXGISurface1 = new IID(1256599698, 25383, 19483, 128, 174, 191, 225, 46, 163, 43, 134);
   public static final IID IDXGIAdapter = new IID(605153249, 4780, 19663, 189, 20, 151, 152, 232, 83, 77, 192);
   public static final IID IDXGIOutput = new IID(-1375539493, 50997, 18064, 141, 82, 90, 141, 194, 2, 19, 170);
   public static final IID IDXGISwapChain = new IID(822949536, 53991, 19466, 170, 4, 106, 157, 35, 184, 136, 106);
   public static final IID IDXGIFactory = new IID(2071029484, 8647, 17582, 178, 26, 201, 174, 50, 26, 227, 105);
   public static final IID IDXGIDevice = new IID(1424783354, 4983, 17638, 140, 50, 136, 253, 95, 68, 200, 76);
   public static final IID IDXGIFactory1 = new IID(1997188728, 62063, 19898, 168, 41, 37, 60, 131, 209, 179, 135);
   public static final IID IDXGIAdapter1 = new IID(688099169, 14393, 17958, 145, 253, 8, 104, 121, 1, 26, 5);
   public static final IID IDXGIDevice1 = new IID(2010879759, 25206, 18618, 186, 40, 7, 1, 67, 180, 57, 44);
   public static final IID IDXGIDisplayControl = new IID(-358760678, 51342, 17542, 133, 74, 152, 170, 1, 56, 243, 12);
   public static final IID IDXGIOutputDuplication = new IID(421329603, 41793, 18189, 178, 110, 168, 100, 244, 40, 49, 156);
   public static final IID IDXGISurface2 = new IID(-1415276835, 46615, 19640, 168, 102, 188, 68, 215, 235, 31, 162);
   public static final IID IDXGIResource1 = new IID(815141753, 17929, 19009, 153, 142, 84, 254, 86, 126, 224, 193);
   public static final IID IDXGIDevice2 = new IID(83920407, 64509, 16465, 167, 144, 20, 72, 132, 180, 246, 169);
   public static final IID IDXGISwapChain1 = new IID(2030716407, 3394, 18550, 152, 58, 10, 85, 207, 230, 244, 170);
   public static final IID IDXGIFactory2 = new IID(1355299356, 57458, 19528, 135, 176, 54, 48, 250, 54, 166, 208);
   public static final IID IDXGIAdapter2 = new IID(178368010, 64014, 19332, 134, 68, 224, 95, 248, 229, 172, 181);
   public static final IID IDXGIOutput1 = new IID(13491880, 37787, 19331, 163, 64, 166, 133, 34, 102, 102, 204);
   public static final IID IDXGIDevice3 = new IID(1611106668, 12868, 19197, 191, 24, 166, 211, 190, 218, 80, 35);
   public static final IID IDXGISwapChain2 = new IID(-1463932220, 6559, 18758, 179, 49, 121, 89, 159, 185, 141, 231);
   public static final IID IDXGIOutput2 = new IID(1499347409, 10020, 18019, 153, 177, 218, 150, 157, 226, 131, 100);
   public static final IID IDXGIFactory3 = new IID(625489955, 52550, 19581, 134, 202, 71, 170, 149, 184, 55, 189);
   public static final IID IDXGIDecodeSwapChain = new IID(640878187, 17684, 19578, 143, 216, 18, 234, 152, 5, 157, 24);
   public static final IID IDXGIFactoryMedia = new IID(1105711602, 42385, 20347, 162, 229, 250, 156, 132, 62, 28, 18);
   public static final IID IDXGISwapChainMedia = new IID(-577390325, 61535, 20330, 189, 101, 37, 191, 178, 100, 189, 132);
   public static final IID IDXGIOutput3 = new IID(-1972653311, 32382, 16884, 168, 224, 91, 50, 247, 249, 155, 24);
   public static final IID IDXGISwapChain3 = new IID(-1797678117, 61944, 19120, 178, 54, 125, 160, 23, 14, 218, 177);
   public static final IID IDXGIOutput4 = new IID(-595736011, 8598, 16717, 159, 83, 97, 120, 132, 3, 42, 96);
   public static final IID IDXGIFactory4 = new IID(466020866, 61238, 17999, 191, 12, 33, 202, 57, 229, 22, 138);
   public static final IID IDXGIAdapter3 = new IID(1683580836, 5010, 17168, 167, 152, 128, 83, 206, 62, 147, 253);
   public static final IID IDXGIOutput5 = new IID(-2136968156, 43858, 17131, 131, 60, 12, 66, 253, 40, 45, 152);
   public static final IID IDXGISwapChain4 = new IID(1029201242, 48458, 18590, 177, 244, 61, 188, 182, 69, 47, 251);
   public static final IID IDXGIDevice4 = new IID(-1783301793, 55514, 19620, 158, 230, 59, 118, 213, 150, 138, 16);
   public static final IID IDXGIFactory5 = new IID(1983046133, 61029, 19914, 135, 253, 132, 205, 117, 248, 131, 141);
   public static final IID IDXGIAdapter4 = new IID(1015912913, 20415, 16769, 168, 44, 175, 102, 191, 123, 210, 78);
   public static final IID IDXGIOutput6 = new IID(109266664, 43756, 19332, 173, 215, 19, 127, 81, 63, 119, 161);
   public static final IID IDXGIFactory6 = new IID(-1045010097, 65289, 17577, 176, 60, 119, 144, 10, 10, 29, 23);
   public static final IID IDXGIFactory7 = new IID(-1533645075, 30427, 17626, 132, 193, 238, 154, 122, 251, 32, 168);
   public static final IID ID3D12Object = new IID(-989937009, 31078, 20117, 159, 148, 244, 49, 203, 86, 195, 184);
   public static final IID ID3D12DeviceChild = new IID(-1872905909, 40972, 16704, 157, 245, 43, 100, 202, 158, 163, 87);
   public static final IID ID3D12RootSignature = new IID(-984978586, 29407, 20200, 139, 229, 169, 70, 161, 66, 146, 20);
   public static final IID ID3D12RootSignature1 = new IID(-1013924483, 37186, 19093, 176, 114, 109, 52, 57, 173, 229, 196);
   public static final IID ID3D12RootSignatureDeserializer = new IID(883647611, 15560, 18092, 132, 27, 192, 150, 86, 69, 192, 70);
   public static final IID ID3D12VersionedRootSignatureDeserializer = new IID(2140261991, 2316, 19383, 183, 142, 237, 143, 242, 227, 29, 160);
   public static final IID ID3D12Pageable = new IID(1676564731, 4712, 18485, 134, 218, 240, 8, 206, 98, 240, 214);
   public static final IID ID3D12Heap = new IID(1799038210, 28241, 17843, 144, 238, 152, 132, 38, 94, 141, 243);
   public static final IID ID3D12Resource = new IID(1768178366, 42798, 16473, 188, 121, 91, 92, 152, 4, 15, 173);
   public static final IID ID3D12CommandAllocator = new IID(1627578084, 44889, 19209, 185, 153, 180, 77, 115, 240, 155, 36);
   public static final IID ID3D12Fence = new IID(175455695, 50392, 19345, 173, 246, 190, 90, 96, 217, 90, 118);
   public static final IID ID3D12Fence1 = new IID(1127646718, 57899, 19616, 168, 219, 181, 180, 244, 221, 14, 74);
   public static final IID ID3D12PipelineState = new IID(1985622259, 63012, 19567, 168, 40, 172, 233, 72, 98, 36, 69);
   public static final IID ID3D12PipelineState1 = new IID(1447460940, 38456, 18679, 145, 130, 179, 238, 90, 107, 96, 251);
   public static final IID ID3D12DescriptorHeap = new IID(-1896134883, 24940, 20297, 144, 247, 18, 123, 183, 99, 250, 81);
   public static final IID ID3D12QueryHeap = new IID(227956910, 60741, 18078, 166, 29, 151, 14, 197, 131, 202, 180);
   public static final IID ID3D12CommandSignature = new IID(-1016432260, 60544, 20234, 137, 133, 167, 178, 71, 80, 130, 209);
   public static final IID ID3D12CommandList = new IID(1897322780, 59364, 18382, 184, 198, 236, 129, 104, 244, 55, 229);
   public static final IID ID3D12GraphicsCommandList = new IID(1528171791, 44059, 16773, 139, 168, 179, 174, 66, 165, 164, 85);
   public static final IID ID3D12GraphicsCommandList1 = new IID(1429275643, 8167, 17751, 187, 56, 148, 109, 125, 14, 124, 167);
   public static final IID ID3D12GraphicsCommandList2 = new IID(952362373, 65303, 16684, 145, 80, 79, 198, 249, 215, 42, 40);
   public static final IID ID3D12CommandQueue = new IID(248017062, 23934, 19490, 140, 252, 91, 170, 224, 118, 22, 237);
   public static final IID ID3D12CommandQueue1 = new IID(977023333, 3815, 19342, 160, 175, 99, 86, 180, 195, 187, 185);
   public static final IID ID3D12Device = new IID(412621297, 7606, 19287, 190, 84, 24, 33, 51, 155, 133, 247);
   public static final IID ID3D12PipelineLibrary = new IID(-968743256, 37377, 18095, 180, 204, 83, 251, 159, 247, 65, 79);
   public static final IID ID3D12PipelineLibrary1 = new IID(-2132099262, 9576, 20062, 189, 130, 195, 127, 134, 150, 29, 195);
   public static final IID ID3D12Device1 = new IID(2007813760, 25486, 20069, 136, 149, 193, 242, 51, 134, 134, 62);
   public static final IID ID3D12Device2 = new IID(817538078, 45403, 18268, 160, 187, 26, 245, 197, 182, 67, 40);
   public static final IID ID3D12Device3 = new IID(-2116363243, 11181, 17298, 147, 197, 16, 19, 69, 196, 170, 152);
   public static final IID ID3D12ProtectedSession = new IID(-1588380392, 2753, 16516, 133, 185, 137, 169, 97, 22, 128, 107);
   public static final IID ID3D12ProtectedResourceSession = new IID(1826002676, 62089, 16588, 128, 145, 90, 108, 10, 9, 156, 61);
   public static final IID ID3D12Device4 = new IID(-395976937, 43502, 18169, 164, 99, 48, 152, 49, 90, 162, 229);
   public static final IID ID3D12LifetimeOwner = new IID(-429412449, 52566, 20294, 131, 206, 3, 46, 89, 93, 112, 168);
   public static final IID ID3D12SwapChainAssistant = new IID(-237017930, 22525, 18893, 136, 7, 192, 235, 136, 180, 92, 143);
   public static final IID ID3D12LifetimeTracker = new IID(1070611766, 20145, 16970, 165, 130, 73, 78, 203, 139, 168, 19);
   public static final IID ID3D12StateObject = new IID(1191274819, 64680, 17812, 147, 234, 175, 37, 139, 85, 52, 109);
   public static final IID ID3D12StateObjectProperties = new IID(-564156377, 39929, 20262, 137, 255, 215, 245, 111, 222, 56, 96);
   public static final IID ID3D12StateObjectProperties1 = new IID(1175235271, 7460, 17514, 161, 132, 202, 103, 219, 73, 65, 56);
   public static final IID ID3D12StateObjectProperties2 = new IID(-706205417, 61681, 17615, 174, 94, 206, 34, 45, 208, 184, 132);
   public static final IID ID3D12WorkGraphProperties = new IID(106614641, 63587, 19337, 130, 244, 2, 228, 213, 136, 103, 87);
   public static final IID ID3D12Device5 = new IID(-1957750981, 12266, 19328, 143, 88, 67, 7, 25, 26, 185, 93);
   public static final IID ID3D12DeviceRemovedExtendedDataSettings = new IID(-2101589988, 27547, 16432, 174, 219, 126, 227, 209, 223, 30, 99);
   public static final IID ID3D12DeviceRemovedExtendedDataSettings1 = new IID(-606753199, 13079, 20234, 173, 249, 29, 124, 237, 202, 174, 11);
   public static final IID ID3D12DeviceRemovedExtendedDataSettings2 = new IID(1632969608, 427, 16392, 164, 54, 131, 219, 24, 149, 102, 234);
   public static final IID ID3D12DeviceRemovedExtendedData = new IID(-1735189197, 23272, 18321, 170, 60, 26, 115, 162, 147, 78, 113);
   public static final IID ID3D12DeviceRemovedExtendedData1 = new IID(-1759010782, 53021, 19930, 158, 186, 239, 250, 101, 63, 197, 6);
   public static final IID ID3D12DeviceRemovedExtendedData2 = new IID(1744590870, 58570, 18709, 191, 24, 66, 84, 18, 114, 218, 84);
   public static final IID ID3D12Device6 = new IID(-955571685, 16612, 18967, 137, 175, 2, 90, 7, 39, 166, 220);
   public static final IID ID3D12ProtectedResourceSession1 = new IID(-688837162, 30459, 16494, 137, 97, 66, 150, 238, 252, 4, 9);
   public static final IID ID3D12Device7 = new IID(1543588691, 26785, 19355, 139, 209, 221, 96, 70, 185, 53, 139);
   public static final IID ID3D12Device8 = new IID(-1843861829, 63812, 20350, 167, 92, 177, 178, 199, 183, 1, 243);
   public static final IID ID3D12Resource1 = new IID(-1654775174, 17456, 16737, 136, 179, 62, 202, 107, 177, 110, 25);
   public static final IID ID3D12Resource2 = new IID(-1103696837, 60037, 19179, 164, 90, 233, 215, 100, 4, 164, 149);
   public static final IID ID3D12Heap1 = new IID(1462727561, 8552, 18915, 150, 147, 214, 223, 88, 113, 191, 109);
   public static final IID ID3D12GraphicsCommandList3 = new IID(1876591527, 47180, 20024, 154, 200, 199, 189, 34, 1, 107, 61);
   public static final IID ID3D12MetaCommand = new IID(-608678873, 14030, 20425, 184, 1, 240, 72, 196, 106, 197, 112);
   public static final IID ID3D12GraphicsCommandList4 = new IID(-2024525426, 54185, 17729, 152, 207, 100, 91, 80, 220, 72, 116);
   public static final IID ID3D12ShaderCacheSession = new IID(685918557, 3940, 19172, 166, 236, 18, 146, 85, 220, 73, 168);
   public static final IID ID3D12Device9 = new IID(1283516770, 61490, 20320, 188, 158, 235, 194, 207, 161, 216, 60);
   public static final IID ID3D12Device10 = new IID(1367312152, 43622, 18937, 176, 43, 167, 171, 137, 192, 96, 49);
   public static final IID ID3D12Device11 = new IID(1409663812, 54359, 17486, 180, 221, 35, 102, 228, 90, 238, 57);
   public static final IID ID3D12Device12 = new IID(1526056242, 19601, 19664, 181, 65, 21, 164, 5, 57, 95, 197);
   public static final IID ID3D12Device13 = new IID(351195132, 19960, 16631, 161, 24, 92, 129, 111, 69, 105, 94);
   public static final IID ID3D12Device14 = new IID(1601067309, 55445, 17602, 142, 74, 136, 173, 73, 38, 211, 35);
   public static final IID ID3D12Device15 = new IID(1993340783, 7835, 17488, 140, 220, 52, 241, 175, 120, 142, 91);
   public static final IID ID3D12StateObjectDatabase = new IID(-983539529, 46588, 16693, 152, 224, 161, 233, 153, 126, 172, 224);
   public static final IID ID3D12VirtualizationGuestDevice = new IID(-1134111896, 29555, 18755, 135, 87, 252, 135, 220, 121, 228, 118);
   public static final IID ID3D12Tools = new IID(1886511600, 59467, 19251, 151, 79, 18, 250, 73, 222, 101, 197);
   public static final IID ID3D12Tools1 = new IID(-453263335, 56636, 17377, 143, 50, 127, 100, 149, 117, 240, 160);
   public static final IID ID3D12Tools2 = new IID(30643141, 51632, 17057, 149, 140, 194, 107, 2, 212, 208, 151);
   public static final IID ID3D12RuntimeValidationControl = new IID(-955856879, 13923, 19441, 145, 185, 30, 138, 124, 17, 74, 185);
   public static final IID ID3D12PageableTools = new IID(-1894557221, 55505, 17145, 181, 207, 121, 244, 203, 173, 13, 61);
   public static final IID ID3D12DeviceTools = new IID(782667420, 6595, 20039, 161, 9, 108, 218, 223, 240, 172, 169);
   public static final IID ID3D12DeviceTools1 = new IID(-485580857, 58945, 19822, 138, 129, 157, 217, 32, 110, 196, 122);
   public static final IID ID3D12SDKConfiguration = new IID(-370453740, 13226, 17074, 167, 24, 215, 127, 88, 177, 241, 199);
   public static final IID ID3D12SDKConfiguration1 = new IID(-1968205053, 44325, 18617, 154, 87, 217, 195, 126, 0, 157, 159);
   public static final IID ID3D12DeviceFactory = new IID(1643317203, 54094, 20092, 131, 116, 59, 164, 222, 35, 204, 203);
   public static final IID ID3D12DeviceConfiguration = new IID(2027681915, 63334, 16939, 166, 28, 200, 196, 70, 189, 185, 173);
   public static final IID ID3D12DeviceConfiguration1 = new IID(-315349950, 25411, 19990, 187, 130, 163, 165, 119, 135, 78, 86);
   public static final IID ID3D12StateObjectDatabaseFactory = new IID(-172988688, 25738, 17937, 189, 65, 39, 253, 9, 72, 185, 235);
   public static final IID ID3D12ApplicationIdentity = new IID(-2099483515, 29307, 19085, 145, 105, 219, 108, 227, 233, 117, 160);
   public static final IID ID3D12GraphicsCommandList5 = new IID(1426393177, 16420, 18252, 135, 245, 100, 114, 234, 238, 68, 234);
   public static final IID ID3D12GraphicsCommandList6 = new IID(-1014859632, 58696, 19706, 150, 207, 86, 137, 169, 55, 15, 128);
   public static final IID ID3D12GraphicsCommandList7 = new IID(-585690589, 35681, 18281, 144, 227, 22, 12, 205, 228, 226, 193);
   public static final IID ID3D12GraphicsCommandList8 = new IID(-292327687, 22941, 19752, 147, 142, 35, 196, 173, 5, 206, 81);
   public static final IID ID3D12GraphicsCommandList9 = new IID(887957512, 65510, 19499, 177, 26, 202, 189, 43, 12, 89, 225);
   public static final IID ID3D12GraphicsCommandList10 = new IID(1880342549, 53601, 19299, 160, 140, 35, 133, 82, 221, 138, 204);
   public static final IID ID3D12DSRDeviceFactory = new IID(-213659232, 45027, 17311, 177, 61, 205, 135, 164, 59, 112, 202);
   public static final IID ID3D12GBVDiagnostics = new IID(1501136299, 39797, 19899, 190, 35, 7, 97, 25, 91, 235, 238);
   public static final IID ID3D12DeviceStatistics = new IID(1029480872, 41886, 17945, 149, 224, 249, 176, 164, 3, 64, 245);
   public static final IID ID3D12Debug = new IID(876906679, 26694, 18251, 185, 137, 240, 39, 68, 130, 69, 224);
   public static final IID ID3D12Debug1 = new IID(-1342528310, 25598, 19854, 184, 173, 21, 144, 0, 175, 67, 4);
   public static final IID ID3D12Debug2 = new IID(-1817811516, 41906, 20061, 182, 146, 162, 106, 225, 78, 51, 116);
   public static final IID ID3D12Debug3 = new IID(1559553423, 63089, 20465, 165, 66, 54, 134, 227, 209, 83, 209);
   public static final IID ID3D12Debug4 = new IID(21725550, 40645, 18991, 168, 69, 255, 190, 68, 28, 225, 58);
   public static final IID ID3D12Debug5 = new IID(1418554130, 2554, 16608, 144, 105, 93, 205, 88, 154, 82, 201);
   public static final IID ID3D12Debug6 = new IID(-2102913322, 23809, 16727, 151, 208, 73, 117, 70, 63, 209, 237);
   public static final IID ID3D12DebugDevice1 = new IID(-1447618704, 53401, 19045, 166, 152, 61, 238, 16, 2, 15, 136);
   public static final IID ID3D12DebugDevice = new IID(1072420573, 18803, 18311, 129, 148, 228, 95, 158, 40, 146, 62);
   public static final IID ID3D12DebugDevice2 = new IID(1626131393, 14221, 19953, 137, 76, 248, 172, 92, 228, 215, 221);
   public static final IID ID3D12DebugCommandQueue = new IID(165723958, 21676, 18511, 136, 71, 75, 174, 234, 182, 5, 58);
   public static final IID ID3D12DebugCommandQueue1 = new IID(381564322, 49110, 18930, 188, 174, 234, 174, 74, 255, 134, 45);
   public static final IID ID3D12DebugCommandList1 = new IID(271362385, 12571, 19201, 177, 31, 236, 184, 62, 6, 27, 55);
   public static final IID ID3D12DebugCommandList = new IID(165723958, 21676, 18511, 136, 71, 75, 174, 234, 182, 5, 63);
   public static final IID ID3D12DebugCommandList2 = new IID(-1363839537, 19974, 18622, 186, 59, 196, 80, 252, 150, 101, 46);
   public static final IID ID3D12DebugCommandList3 = new IID(427646485, 19767, 19764, 175, 120, 114, 76, 215, 15, 219, 31);
   public static final IID ID3D12SharingContract = new IID(182418770, 37532, 20065, 173, 219, 255, 237, 48, 222, 102, 239);
   public static final IID ID3D12ManualWriteTrackingResource = new IID(-2033566843, 18861, 19310, 174, 213, 237, 219, 24, 84, 15, 65);
   public static final IID ID3D12InfoQueue = new IID(121809163, 50055, 18495, 185, 70, 48, 167, 228, 230, 20, 88);
   public static final IID ID3D12InfoQueue1 = new IID(676519304, 46212, 19468, 182, 177, 103, 22, 133, 0, 230, 0);

   public IID(final int data1, final int data2, final int data3, final int data4_1, final int data4_2, final int data4_3, final int data4_4, final int data4_5, final int data4_6, final int data4_7, final int data4_8) {
      assert ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;

      checkShort(data2);
      checkShort(data3);
      checkByte(data4_1);
      checkByte(data4_2);
      checkByte(data4_3);
      checkByte(data4_4);
      checkByte(data4_5);
      checkByte(data4_6);
      checkByte(data4_7);
      checkByte(data4_8);
      long first8Bytes = Integer.toUnsignedLong(data1);
      first8Bytes |= (long)(data2 & '\uffff') << 32;
      first8Bytes |= (long)(data3 & '\uffff') << 48;
      long second8Bytes = 0L;
      second8Bytes |= (long)(data4_1 & 255) << 0;
      second8Bytes |= (long)(data4_2 & 255) << 8;
      second8Bytes |= (long)(data4_3 & 255) << 16;
      second8Bytes |= (long)(data4_4 & 255) << 24;
      second8Bytes |= (long)(data4_5 & 255) << 32;
      second8Bytes |= (long)(data4_6 & 255) << 40;
      second8Bytes |= (long)(data4_7 & 255) << 48;
      second8Bytes |= (long)(data4_8 & 255) << 56;
      this(first8Bytes, second8Bytes);
   }

   public IID {
      super();
   }

   private static void checkShort(final int value) {
      if (value < 0 || value > 65535) {
         throw new IllegalArgumentException("Argument out of range for short");
      }
   }

   private static void checkByte(final int value) {
      if (value < 0 || value > 255) {
         throw new IllegalArgumentException("Argument out of range for byte");
      }
   }

   public void write(final MemorySegment segment) {
      segment.set(ValueLayout.JAVA_LONG, 0L, this.first8Bytes);
      segment.set(ValueLayout.JAVA_LONG, 8L, this.second8Bytes);
   }

   public MemorySegment allocate(final Arena arena) {
      MemorySegment segment = arena.allocate(16L, 4L);
      this.write(segment);
      return segment;
   }

   public String toString() {
      StringBuilder value = new StringBuilder();

      for(int i = 0; i < 8; ++i) {
         value.append(Long.toHexString(this.first8Bytes >> i * 8 & 255L));
      }

      for(int i = 0; i < 8; ++i) {
         value.append(Long.toHexString(this.second8Bytes >> i * 8 & 255L));
      }

      return value.toString();
   }
}
