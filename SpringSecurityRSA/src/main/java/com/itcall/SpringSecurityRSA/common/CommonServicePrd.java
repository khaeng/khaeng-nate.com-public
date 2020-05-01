package com.itcall.SpringSecurityRSA.common;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Profile({"prd", "prod"})
@Service("commonService")
public class CommonServicePrd extends CommonServiceDef {

	@Override
	public Map<String, String> getCommonMap(HttpServletRequest request, Model model) {
		Map<String, String> result = super.getCommonMap(request, model);
		result.put("profile", "prd");
		return result;
	}

}
