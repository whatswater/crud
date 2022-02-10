package com.whatswater.curd.project.sys.employeeFilter;


import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.sys.employeeFilter.SExpressionUtil.SExpression;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EmployeeFilterService {
    private final EmployeeFilterSQL employeeFilterSQL;
    private EmployeeFilterDataService employeeFilterDataService;

    public EmployeeFilterService(MySQLPool pool) {
        this.employeeFilterSQL = new EmployeeFilterSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<EmployeeFilter>> search(Page page, EmployeeFilterQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return employeeFilterSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return employeeFilterSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(EmployeeFilter::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<EmployeeFilter> getById(Long employeeFilterId) {
        Future<JsonObject> result = employeeFilterSQL.selectById(employeeFilterId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new EmployeeFilter(json);
        });
    }

    public Future<EmployeeFilter> getByCode(String code) {
        SqlAssist sqlAssist = EmployeeFilter.codeSqlAssist(code);

        return employeeFilterSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return null;
            }
            return new EmployeeFilter(jsonList.get(0));
        });
    }

    public Future<List<EmployeeFilterData>> queryDataByCode(String code) {
        return employeeFilterDataService.queryByCode(code);
    }


    public Future<SExpression> getSExpressionByCode(String code) {
        return employeeFilterDataService.queryByCode(code).map(SExpressionUtil::parse);
    }

    public Future<List<Long>> insertTreeWithCheck(EmployeeFilterTreeVo treeVo) {
        List<EmployeeFilterData> employeeFilterDataList = treeVo.toFilterDataList();
        if (CollectionUtil.isEmpty(employeeFilterDataList)) {
            return Future.failedFuture("过滤条件数据为空");
        }
        boolean hasUnidentifiedValue = employeeFilterDataList.stream().anyMatch(employeeFilterData -> Objects.isNull(employeeFilterData.getValueType()));
        if (hasUnidentifiedValue) {
            return Future.failedFuture("过滤条件存在未知的值类型");
        }

        return getByCode(treeVo.getCode()).compose(dbEmployeeFilter -> {
            if (dbEmployeeFilter != null) {
                return Future.failedFuture("系统已经存在此编码的用户过滤条件数据");
            }
            return Future.succeededFuture();
        }).compose(v -> {
            EmployeeFilter employeeFilter = treeVo.toFilter();
            employeeFilter.setCreateTime(LocalDateTime.now());
            return insert(employeeFilter);
        }).compose(v -> {
            return employeeFilterDataService.batchInsert(employeeFilterDataList);
        });
    }

    public Future<List<Long>> updateTreeWithCheck(EmployeeFilterTreeVo treeVo) {
        List<EmployeeFilterData> newDataList = treeVo.toFilterDataList();
        if (CollectionUtil.isEmpty(newDataList)) {
            return Future.failedFuture("过滤条件数据为空");
        }
        boolean hasUnidentifiedValue = newDataList.stream().anyMatch(employeeFilterData -> Objects.isNull(employeeFilterData.getValueType()));
        if (hasUnidentifiedValue) {
            return Future.failedFuture("过滤条件存在未知的值类型");
        }

        return getByCode(treeVo.getCode()).compose(dbEmployeeFilter -> {
            if (dbEmployeeFilter == null) {
                return Future.failedFuture("系统不存在此编码的用户过滤条件数据");
            }
            return Future.succeededFuture(dbEmployeeFilter);
        }).compose(dbEmployeeFilter -> {
            EmployeeFilter update = new EmployeeFilter();
            update.setId(dbEmployeeFilter.getId());
            update.setRemark(treeVo.getRemark());
            return update(update);
        }).compose(v -> {
            return employeeFilterDataService.deleteByCode(treeVo.getCode());
        }).compose(l -> {
            return employeeFilterDataService.batchInsert(newDataList);
        });
    }

    public Future<Integer> deleteWithCheck(String code) {
        return getByCode(code).compose(dbEmployeeFilter -> {
            if (dbEmployeeFilter == null) {
                return Future.failedFuture("系统不存在此编码的用户过滤条件数据");
            }
            return employeeFilterDataService.deleteByCode(code);
        }).compose(l -> {
            return this.deleteByCode(code);
        });
    }

    public Future<Integer> deleteByCode(String code) {
        SqlAssist sqlAssist = EmployeeFilter.codeSqlAssist(code);
        return employeeFilterSQL.deleteByAssist(sqlAssist);
    }

    public Future<Long> insert(EmployeeFilter employeeFilter) {
        return employeeFilterSQL.insertNonEmptyGeneratedKeys(employeeFilter, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(EmployeeFilter employeeFilter) {
        return employeeFilterSQL.updateNonEmptyById(employeeFilter);
    }

    public void setEmployeeFilterDataService(EmployeeFilterDataService employeeFilterDataService) {
        this.employeeFilterDataService = employeeFilterDataService;
    }
}
