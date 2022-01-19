package kr.co.itcall.util;

public class StringUtil {

	public static void main(String[] args) {
		String testStr = "	tgtGrid.dataSource.add(data);\n"
				+ "	\n"
				+ "/*//	//서식지 - 에이전트 필수 문서\n"
				+ "	var fxdformData = lfn_getGridData(\"#fxdFormDocList\")\n"
				+ "	for(var i = 0; i< fxdformData.length; i++) {\n"
				+ "		//처음 서식지 불러올때 조건문서 -> 선택문서로 바꿔서 선택문서에서 Agent용을 찾는다.\n"
				+ "		if(fxdformData[i].fxdformTypeCd === \"1002\" && (fxdformData[i].frmpapNm.indexOf(\"\\(Agent용\\)\") > 1) ) {\n"
				+ "			fxdformData[i].fxdformTypeCd = \"1001\" ;\n"
				+ "			fxdformData[i].fxdformTypeNm = \"필수문서\";\n"
				+ "		}\n"
				+ "	}\n"
				+ "	lfn_getGrid(\"#fxdFormDocList\").dataSource.data(fxdformData);*/\n"
				+ "	\n"
				+ "//	//서식지 - 에이전트 필수 문서\n"
				+ "	var fxdformData = lfn_getGridData(\"#fxdFormDocList\");\n"
				+ "";
		System.out.println(remarkClear(testStr));
	}
	public static String remarkClear(String src) {
			
			String result = "";
			boolean isSConst = false;  // '.....' Single String
			boolean isDConst = false;  // "....." Duble String
			boolean isExConst = false; // \? Exceptor String
			boolean isRegular = false; // /..../g Reqular Expression
			boolean isSRemark = false; // //      Single-line-Remark.
			int idxMRemark = -1;       // /*...*/ Multi-line-Remark.
				
			for (int index = 0; index < src.length(); index++) {
//			for (int index : src.toCharArray()) {
				char chr = src.charAt(index);
				char beChr = index<=0? 32 :src.charAt(index-1); // "" == 32
				char afChr = index+1>=src.length()? 32 :src.charAt(index+1); // "" == 32
				if(isSRemark && chr=='\n'){
					isSRemark = false;result+=chr;continue;
				}
				if(idxMRemark>1 && beChr=='*' && chr=='/'){
					idxMRemark = -1;continue;
				}
				if(isSRemark){
					continue;
				}else if(idxMRemark>-1){
					++idxMRemark;continue;
				}
				if(isExConst){
					isExConst = false;result+=chr;continue;
				}else if((isSConst || isDConst || isRegular) && chr=='\\'){
					isExConst = true;result+=chr;continue;
				}
				if(isSConst && beChr!='\\' && chr=='\''){
					isSConst = false;result+=chr;continue;
				}
				if(isDConst && beChr!='\\' && chr=='"'){
					isDConst = false;result+=chr;continue;
				}
				if(isRegular && beChr=='/' && chr=='g'){
					isRegular = false;result+=chr;continue;
				}
				if(!(isSConst || isDConst || isRegular)){
					if(chr=='/' && afChr=='/'){
						isSRemark = true;continue;
					}
					if(chr=='/' && afChr=='*'){
						idxMRemark = 0;continue;
					}
					if(chr=='\''){
						isSConst = true;
					}else if(chr=='"'){
						isDConst = true;
					}else if(chr=='/'){
						isRegular = true;
					}
					result+=chr;continue;
				}
				result+=chr;continue;
			}
			return result;
	}

}
