package com.zf1976.mayi.upms.biz.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.common.log.annotation.Log;
import com.zf1976.mayi.upms.biz.pojo.dto.role.RoleDTO;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.query.RoleQueryParam;
import com.zf1976.mayi.upms.biz.pojo.vo.role.RoleVO;
import com.zf1976.mayi.upms.biz.service.SysRoleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author mac
 * @date 2020/11/22
 **/
@RestController
@RequestMapping("/api/roles")
public class SysRoleController {

    private final SysRoleService service;

    public SysRoleController(SysRoleService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public DataResult<IPage<RoleVO>> selectAllRole() {
        return DataResult.success(this.service.selectAllRole());
    }

    @PostMapping("/page")
    public DataResult<IPage<RoleVO>> selectRolePage(@RequestBody Query<RoleQueryParam> query) {
        return DataResult.success(service.selectRolePage(query));
    }

    @Log(description = "根据id查询角色")
    @PostMapping("/{id}")
    public DataResult<RoleVO> selectRole(@PathVariable("id") @NotNull Long id) {
        return DataResult.success(service.selectRole(id));
    }

    @GetMapping("/level")
    public DataResult<Integer> getRoleLevel() {
        return DataResult.success(service.selectRoleLevel());
    }

    @Log(description = "修改角色状态")
    @PatchMapping("/status")
    public DataResult<Void> setRoleStatus(@RequestParam @NotNull Long id, @RequestParam @NotNull Boolean enabled) {
        return DataResult.success(service.updateRoleStatus(id, enabled));
    }

    @Log(description = "新增角色")
    @PostMapping("/save")
    public DataResult<Void> saveRole(@RequestBody @Validated(ValidationInsertGroup.class) RoleDTO dto) {
        return DataResult.success(service.savaRole(dto));
    }

    @Log(description = "更新角色")
    @PutMapping("/update")
    public DataResult<Void> updateRole(@RequestBody @Validated(ValidationUpdateGroup.class) RoleDTO dto) {
        return DataResult.success(service.updateRole(dto));
    }

    @Log(description = "删除角色")
    @DeleteMapping("/delete")
    public DataResult<Void> deleteRole(@RequestBody Set<Long> ids) {
        return DataResult.success(service.deleteRole(ids));
    }
}
