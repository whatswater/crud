package com.whatswater.curd.shell;


import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.ext.shell.command.Command;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandResolver;
import io.vertx.ext.shell.spi.CommandResolverFactory;

import java.util.ArrayList;
import java.util.List;

public class ModuleCommandPack implements CommandResolverFactory {
    @Override
    public void resolver(Vertx vertx, Handler<AsyncResult<CommandResolver>> handler) {
        CLI cli = CLI.create("module")
            .addArgument(new Argument().setArgName("operation"));
        CommandBuilder command = CommandBuilder.command(cli);

        command.processHandler(process -> {
            CommandLine commandLine = process.commandLine();
            String argValue = commandLine.getArgumentValue(0);

            if ("list".equals(argValue)) {
                process.write("1111111111111111");
            }
            process.end();
        });

        List<Command> commands = new ArrayList<>();
        commands.add(command.build(vertx));

        handler.handle(Future.succeededFuture(() -> commands));
    }
}
