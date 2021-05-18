package com.zf1976.ant.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.ant.common.core.foundation.DataResult;
import com.zf1976.ant.upms.biz.pojo.dto.position.PositionDTO;
import com.zf1976.ant.upms.biz.pojo.query.PositionQueryParam;
import com.zf1976.ant.upms.biz.pojo.query.Query;
import com.zf1976.ant.common.core.validate.ValidationInsertGroup;
import com.zf1976.ant.common.core.validate.ValidationUpdateGroup;
import com.zf1976.ant.upms.biz.pojo.vo.job.PositionVO;
import com.zf1976.ant.upms.biz.service.SysPositionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

/**
 * @author mac
 * @date 2020/10/25 5:40 下午
 */
@RestController
@RequestMapping("/api/positions")
public class SysPositionController {

    private final SysPositionService service;

    public SysPositionController(SysPositionService service) {
        this.service = service;
    }

    @PostMapping("/page")
    public DataResult<IPage<PositionVO>> selectPositionPage(@RequestBody Query<PositionQueryParam> query) {
        return DataResult.success(service.selectPositionPage(query));
    }

    @PostMapping("/save")
    public DataResult<Optional<Void>> savePosition(@RequestBody @Validated(ValidationInsertGroup.class) PositionDTO dto) {
        return DataResult.success(service.savePosition(dto));
    }

    @PutMapping("/update")
    public DataResult<Optional<Void>> updatePosition(@RequestBody @Validated(ValidationUpdateGroup.class) PositionDTO dto) {
        return DataResult.success(service.updatePosition(dto));
    }
    @DeleteMapping("/delete")
    public DataResult<Optional<Void>> deletePositionList(@RequestBody @NotNull Set<Long> ids) {
        return DataResult.success(service.deletePosition(ids));
    }

    @PostMapping("/download")
    public DataResult<Optional<Void>> downloadPositionExcel(@RequestBody Query<PositionQueryParam> requestPage, HttpServletResponse response) {
        return DataResult.success(service.downloadPositionExcel(requestPage, response));
    }
}
