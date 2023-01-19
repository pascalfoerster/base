package de.featjar.base.cli;

import de.featjar.base.FeatJAR;
import de.featjar.base.data.Result;
import de.featjar.base.data.Void;
import de.featjar.base.log.IndentStringBuilder;
import de.featjar.base.log.Log;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Parses and validates options.
 *
 * @author Elias Kuiter
 */
public interface IOptionInput {
    /**
     * Option for printing usage information.
     */
    Option<LinkedHashSet<ICommand>> COMMAND_OPTION = new Option<>("command",
            Result.mapReturnValue(s -> FeatJAR.extensionPoint(Commands.class).getMatchingExtensions(s)))
            .setDescription("Command(s) to execute")
            .setDefaultValue(new LinkedHashSet<>());

    /**
     * Option for printing usage information.
     */
    Option<Boolean> HELP_OPTION = new Flag("help").setDescription("Print usage information");

    /**
     * Option for printing version information.
     */
    Option<Boolean> VERSION_OPTION =
            new Flag("version").setDescription("Print version information");

    /**
     * Option for setting the logger verbosity.
     */
    Option<Log.Verbosity> VERBOSITY_OPTION = new Option<>("verbosity", Log.Verbosity::of)
            .setDescription("The logger verbosity, one of "
                    + Arrays.stream(Log.Verbosity.values())
                    .map(Objects::toString)
                    .map(String::toLowerCase)
                    .collect(Collectors.joining(", ")))
            .setDefaultValue(Commands.DEFAULT_VERBOSITY);

    /**
     * {@return a void result when the given options are valid in this option input}
     * In particular, returns an empty result when there are unused options in this option input.
     *
     * @param options the options
     */
    Result<Void> validate(List<Option<?>> options);

    /**
     * {@return the value of the given option in this option input}
     *
     * @param option the option
     * @param <T> the type of the option value
     */
    <T> Result<T> get(Option<T> option);

    /**
     * {@return the commands supplied in this option input}
     */
    default LinkedHashSet<ICommand> getCommands() {
        return get(COMMAND_OPTION).get();
    }

    /**
     * {@return the general options of this option input}
     */
    default List<Option<?>> getOptions() {
        return List.of(COMMAND_OPTION, HELP_OPTION, VERSION_OPTION, VERBOSITY_OPTION);
    }

    /**
     * {@return the command-line interface help}
     */
    default String getHelp() {
        IndentStringBuilder sb = new IndentStringBuilder();
        List<ICommand> commands = FeatJAR.extensionPoint(Commands.class).getExtensions();
        sb.appendLine("Usage: java -jar " + FeatJAR.LIBRARY_NAME + " <command> [--<flag> | --<option> <value>]...")
                .appendLine();
        if (commands.size() == 0) {
            sb.append("No commands are available. You can register commands in an extensions.xml file when building "
                    + FeatJAR.LIBRARY_NAME + ".\n");
        }
        sb.append("The following commands are available:\n").addIndent();
        for (final ICommand command : commands) {
            sb.appendLine(String.format(
                    "%s: %s",
                    command.getIdentifier(),
                    Result.ofNullable(command.getDescription()).orElse("")));
        }
        sb.removeIndent();
        sb.appendLine();
        sb.appendLine("General options:").addIndent();
        sb.appendLine(getOptions());
        sb.removeIndent();
        for (ICommand command : getCommands()) {
            if (!command.getOptions().isEmpty()) {
                sb.appendLine();
                sb.appendLine(String.format("Options of command %s:", command.getIdentifier()));
                sb.addIndent();
                sb.appendLine(command.getOptions());
                sb.removeIndent();
            }
        }
        return sb.toString();
    }

    /**
     * {@return whether this option input requests help information}
     */
    default boolean isHelp() {
        return get(HELP_OPTION).get();
    }

    /**
     * {@return whether this option input requests version information}
     */
    default boolean isVersion() {
        return get(VERSION_OPTION).get();
    }

    /**
     * {@return the verbosity supplied in this option input}
     */
    default Log.Verbosity getVerbosity() {
        return get(VERBOSITY_OPTION).get();
    }

}
