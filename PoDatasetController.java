package com.sankuai.meituan.seal.web.api;


import com.alibaba.fastjson.JSON;
import com.sankuai.meituan.seal.v2.constant.CaoType;
import com.sankuai.meituan.seal.domain.vo.page.Page;
import com.sankuai.meituan.seal.domain.vo.request.ApiFilterRequest;
import com.sankuai.meituan.seal.domain.vo.request.FilterRequest;
import com.sankuai.meituan.seal.domain.vo.result.Result;
import com.sankuai.meituan.seal.domain.vo.view.CompanyWithBankVo;
import com.sankuai.meituan.seal.domain.vo.view.IdNameVo;
import com.sankuai.meituan.seal.service.dataset.CaoDatasetService;
import com.sankuai.meituan.seal.service.privilege.CaoBpmAuthService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * 上海财务系统PO环境所需dataset api文档
 * <p>
 * wiki参考：
 * 1. http://wiki.sankuai.com/pages/viewpage.action?pageId=502649117
 * 2. http://wiki.sankuai.com/pages/viewpage.action?pageId=502639162
 */
@RestController
@RequestMapping("/api/seal/dataset")
public class PoDatasetController {

    private static final Logger logger = LoggerFactory.getLogger(PoDatasetController.class);

    @Autowired
    private CaoDatasetService caoDatasetService;

    @Autowired
    private CaoBpmAuthService caoBpmAuthService;

    /**
     * 搜索我方主体
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/company", method = RequestMethod.POST)
    public Result company(@RequestBody ApiFilterRequest request) {
        logger.info("company, FilterRequest: {}", JSON.toJSONString(request));
        Page page = FilterRequest.getPage(request);
        List<CompanyWithBankVo> companyList = this.caoDatasetService.getCompanyList(page, request.getFilter(), request.getTenantId());
        logger.info("company, ret=: {}", JSON.toJSONString(companyList));
        return Result.builder().data(companyList, page).success().build();
    }

    /**
     * 合同类别
     *
     * @param request
     * @return 返回值需要调整为ID name
     */
    @RequestMapping(value = "/contractType", method = RequestMethod.POST)
    public Result contractType(@RequestBody ApiFilterRequest request) {
        logger.info("contractType, FilterRequest: {}", JSON.toJSONString(request));
        Page page = FilterRequest.getPage(request);
        List<CaoType> caoTypes = caoBpmAuthService.getAllowedCaoTypes(true);
        List<IdNameVo<Integer, String>> types = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(caoTypes)) {
            for (CaoType type : caoTypes) {
                types.add(new IdNameVo<>(type.getIndex(), type.getName()));
            }
        }
        logger.info("/contractType ret={}", JSON.toJSONString(types));
        return Result.builder().data(types, page).success().build();
    }


}
