package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.upms.biz.pojo.dto.dict.DictDTO;
import com.zf1976.ant.upms.biz.pojo.query.DictQueryParam;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import com.zf1976.ant.common.core.validate.ValidationInsertGroup;
import com.zf1976.ant.common.core.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.vo.dict.DictVO;
import com.zf1976.ant.upms.biz.service.SysDictService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 * @date 2020/10/22 9:14 下午
 */
@RestController
@RequestMapping("/api/dictionaries")
public class SysDictController {

    private final SysDictService service;

    public SysDictController(SysDictService service) {
        this.service = service;
    }

    @PostMapping("/page")
    public DataResult<IPage<DictVO>> selectDictPage(@RequestBody Query<DictQueryParam> requestPage) {
        return DataResult.success(service.selectDictPage(requestPage));
    }

    @PostMapping("/save")
    public DataResult<Optional<Void>> saveDict(@RequestBody @Validated({ValidationInsertGroup.class}) DictDTO dto) {
        return DataResult.success(service.saveDict(dto));
    }

    @PutMapping("/update")
    public DataResult<Optional<Void>> updateDict(@RequestBody @Validated(ValidationUpdateGroup.class) DictDTO dto) {
        return DataResult.success(service.updateDict(dto));
    }

    @DeleteMapping("/delete")
    public DataResult<Optional<Void>> deleteDictList(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteDictList(ids));
    }

    @PostMapping("/download")
    public DataResult<Optional<Void>> downloadDictExcel(@RequestBody Query<DictQueryParam> requestPage, HttpServletResponse response) {
        return DataResult.success(service.downloadDictExcel(requestPage, response));
    }

}
