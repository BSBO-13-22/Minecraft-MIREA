package fun.mirea.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import fun.mirea.velocity.utility.FormatUtils;
import net.kyori.adventure.text.Component;

public final class RegisterCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();
        System.out.println("фыв фыв");
        if (args.length == 2) {
            System.out.println("фыв");
        } else source.sendMessage(Component.text(FormatUtils.colorize("&cИспользуйте: &6/reg <пароль> <повтор>")));
    }
}