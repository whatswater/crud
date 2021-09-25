package com.whatswater.curd.project.user;


import com.whatswater.sql.dialect.Dialect.SqlAndParam;
import com.whatswater.sql.dialect.MysqlDialect;
import com.whatswater.sql.statement.Update;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String updateNameById() {
        Update update = userRepository.updateName("A1", 1);
        MysqlDialect dialect = new MysqlDialect();
        SqlAndParam sqlAndParam = dialect.toSql(update);
        return sqlAndParam.getSql();
    }
}
