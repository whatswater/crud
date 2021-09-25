package com.whatswater.curd.project.user;


import com.whatswater.sql.dialect.Dialect.SqlAndParam;
import com.whatswater.sql.dialect.MysqlDialect;
import com.whatswater.sql.statement.Delete;
import com.whatswater.sql.statement.Update;

public class UserService {
    private final UserRepository userRepository;
    MysqlDialect dialect = new MysqlDialect();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String updateNameById() {
        Update update = userRepository.updateName("A1", 1);
        SqlAndParam sqlAndParam = dialect.toSql(update);
        return sqlAndParam.getSql();
    }

    public String deleteById() {
        Delete delete = userRepository.deleteById(1L);
        SqlAndParam sqlAndParam = dialect.toSql(delete);
        return sqlAndParam.getSql();
    }
}
