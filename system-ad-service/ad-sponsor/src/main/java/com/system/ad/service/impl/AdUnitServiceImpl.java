package com.system.ad.service.impl;

import com.system.ad.service.IAdUnitService;
import com.system.ad.constant.Constants;
import com.system.ad.dao.AdPlanRepository;
import com.system.ad.dao.AdUnitRepository;
import com.system.ad.dao.CreativeRepository;
import com.system.ad.dao.unit_condition.AdUnitDistrictRepository;
import com.system.ad.dao.unit_condition.AdUnitItRepository;
import com.system.ad.dao.unit_condition.AdUnitKeywordRepository;
import com.system.ad.dao.CreativeUnitRepository;
import com.system.ad.entity.AdPlan;
import com.system.ad.entity.AdUnit;
import com.system.ad.entity.unit_condition.AdUnitDistrict;
import com.system.ad.entity.unit_condition.AdUnitIt;
import com.system.ad.entity.unit_condition.AdUnitKeyword;
import com.system.ad.entity.CreativeUnit;
import com.system.ad.exception.AdException;
import com.system.ad.vo.AdUnitDistrictRequest;
import com.system.ad.vo.AdUnitDistrictResponse;
import com.system.ad.vo.AdUnitItRequest;
import com.system.ad.vo.AdUnitItResponse;
import com.system.ad.vo.AdUnitKeywordRequest;
import com.system.ad.vo.AdUnitKeywordResponse;
import com.system.ad.vo.AdUnitRequest;
import com.system.ad.vo.AdUnitResponse;
import com.system.ad.vo.CreativeUnitRequest;
import com.system.ad.vo.CreativeUnitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdUnitServiceImpl implements IAdUnitService {

    private  AdPlanRepository planRepository;
    private  AdUnitRepository unitRepository;

    private  AdUnitKeywordRepository unitKeywordRepository;
    private  AdUnitItRepository unitItRepository;
    private  AdUnitDistrictRepository unitDistrictRepository;

    private  CreativeRepository creativeRepository;
    private  CreativeUnitRepository creativeUnitRepository;

    @Autowired
    public AdUnitServiceImpl(AdPlanRepository planRepository,
                             AdUnitRepository unitRepository,
                             AdUnitKeywordRepository unitKeywordRepository,
                             AdUnitItRepository unitItRepository,
                             AdUnitDistrictRepository unitDistrictRepository,
                             CreativeRepository creativeRepository,
                             CreativeUnitRepository creativeUnitRepository) {
        this.planRepository = planRepository;
        this.unitRepository = unitRepository;
        this.unitKeywordRepository = unitKeywordRepository;
        this.unitItRepository = unitItRepository;
        this.unitDistrictRepository = unitDistrictRepository;
        this.creativeRepository = creativeRepository;
        this.creativeUnitRepository = creativeUnitRepository;
    }

    @Override
    public AdUnitResponse createUnit(AdUnitRequest request) throws AdException {
        if (!request.createValidate()) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        Optional<AdPlan> adPlan = planRepository.findById(request.getPlanId());
        if (!adPlan.isPresent()) {
            throw new AdException(Constants.ErrorMsg.CAN_NOT_FIND_RECORD);
        }

        AdUnit oldAdUnit = unitRepository.findByPlanIdAndUnitName(request.getPlanId(), request.getUnitName()
        );

        if (oldAdUnit != null) {
            throw new AdException(Constants.ErrorMsg.SAME_NAME_UNIT_ERROR);
        }

        AdUnit newAdUnit = unitRepository.save(new AdUnit(request.getPlanId(), request.getUnitName(),
                request.getPositionType(), request.getBudget())
        );

        return new AdUnitResponse(newAdUnit.getId(), newAdUnit.getUnitName());
    }

    @Override
    public AdUnitKeywordResponse createUnitKeyword(AdUnitKeywordRequest request) throws AdException {
        List<Long> unitIds = request.getUnitKeywords().stream()
                .map(AdUnitKeywordRequest.UnitKeyword::getUnitId)
                .collect(Collectors.toList());
        if (!isRelatedUnitExist(unitIds)) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        List<Long> ids = Collections.emptyList();

        //request.getUnitkeywords()并不能获取unitKeywordRepository.save()
        //需要的参数，所以还要重新构造该参数
        List<AdUnitKeyword> unitKeywords = new ArrayList<>();
        if (!CollectionUtils.isEmpty(request.getUnitKeywords())) {
            request.getUnitKeywords().forEach(i -> unitKeywords.add(
                    new AdUnitKeyword(i.getUnitId(), i.getKeyword())
            ));
            ids = unitKeywordRepository.saveAll(unitKeywords).stream()
                    .map(AdUnitKeyword::getId)
                    .collect(Collectors.toList());
        }

        return new AdUnitKeywordResponse(ids);
    }

    @Override
    public AdUnitItResponse createUnitIt(AdUnitItRequest request) throws AdException {
        List<Long> unitIds = request.getUnitIts().stream()
                .map(AdUnitItRequest.UnitIt::getUnitId)
                .collect(Collectors.toList());
        if (!isRelatedUnitExist(unitIds)) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        List<AdUnitIt> unitIts = new ArrayList<>();
        request.getUnitIts().forEach(i -> unitIts.add(
                new AdUnitIt(i.getUnitId(), i.getItTag())
        ));
        List<Long> ids = unitItRepository.saveAll(unitIts).stream()
                .map(AdUnitIt::getId)
                .collect(Collectors.toList());

        return new AdUnitItResponse(ids);
    }

    @Override
    public AdUnitDistrictResponse createUnitDistrict(AdUnitDistrictRequest request) throws AdException {
        List<Long> unitIds = request.getUnitDistricts().stream()
                .map(AdUnitDistrictRequest.UnitDistrict::getUnitId)
                .collect(Collectors.toList());
        if (!isRelatedUnitExist(unitIds)) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        List<AdUnitDistrict> unitDistricts = new ArrayList<>();
        request.getUnitDistricts().forEach(d -> unitDistricts.add(
                new AdUnitDistrict(d.getUnitId(), d.getProvince(),
                        d.getCity())
        ));
        List<Long> ids = unitDistrictRepository.saveAll(unitDistricts)
                .stream().map(AdUnitDistrict::getId)
                .collect(Collectors.toList());

        return new AdUnitDistrictResponse(ids);
    }

    @Override
    public CreativeUnitResponse createCreativeUnit(CreativeUnitRequest request) throws AdException {
        List<Long> unitIds = request.getUnitItems().stream()
                .map(CreativeUnitRequest.CreativeUnitItem::getUnitId)
                .collect(Collectors.toList());
        List<Long> creativeIds = request.getUnitItems().stream()
                .map(CreativeUnitRequest.CreativeUnitItem::getCreativeId)
                .collect(Collectors.toList());

        if (!(isRelatedUnitExist(unitIds) && isRelatedCreativeExist(creativeIds))) {
            throw new AdException(Constants.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        List<CreativeUnit> creativeUnits = new ArrayList<>();
        request.getUnitItems().forEach(i -> creativeUnits.add(
                new CreativeUnit(i.getCreativeId(), i.getUnitId())
        ));

        List<Long> ids = creativeUnitRepository.saveAll(creativeUnits)
                .stream()
                .map(CreativeUnit::getId)
                .collect(Collectors.toList());

        return new CreativeUnitResponse(ids);
    }

    private boolean isRelatedUnitExist(List<Long> unitIds) {

        if (CollectionUtils.isEmpty(unitIds)) {
            return false;
        }

        //逻辑有错误？ 改：new HashSet<>(unitIds).size();-> unitIds.size();?
        return unitRepository.findAllById(unitIds).size() ==
                //new HashSet<>(unitIds).size();
                unitIds.size();

    }

    private boolean isRelatedCreativeExist(List<Long> creativeIds) {
        if (CollectionUtils.isEmpty(creativeIds)) {
            return false;
        }

        return creativeRepository.findAllById(creativeIds).size() ==
                //new HashSet<>(creativeIds).size();
            creativeIds.size();
    }
}
