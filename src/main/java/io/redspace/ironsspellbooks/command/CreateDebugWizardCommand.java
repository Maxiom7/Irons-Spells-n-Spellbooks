package io.redspace.ironsspellbooks.command;

import io.redspace.ironsspellbooks.entity.mobs.debug_wizard.DebugWizard;
import io.redspace.ironsspellbooks.spells.SpellType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.server.command.EnumArgument;

public class CreateDebugWizardCommand {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.irons_spellbooks.create_debug_wizard.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(Commands.literal("createDebugWizard").requires((commandSourceStack) -> {
            return commandSourceStack.hasPermission(2);
        }).then(Commands.argument("spellType", EnumArgument.enumArgument(SpellType.class))
                .then(Commands.argument("spellLevel", IntegerArgumentType.integer(0, 10))
                        .then(Commands.argument("targetsPlayer", BoolArgumentType.bool()).executes((ctx) -> {
                            return createDebugWizard(ctx.getSource(), ctx.getArgument("spellType", SpellType.class), IntegerArgumentType.getInteger(ctx, "spellLevel"), BoolArgumentType.getBool(ctx, "targetsPlayer"));
                        })))));
    }

    private static int createDebugWizard(CommandSourceStack source, SpellType spellType, int spellLevel, boolean targetsPlayer) throws CommandSyntaxException {
        var serverPlayer = source.getPlayer();
        if (serverPlayer != null) {
            var debugWizard = new DebugWizard(EntityRegistry.DEBUG_WIZARD.get(), serverPlayer.level, spellType, spellLevel, targetsPlayer);
            debugWizard.setPos(serverPlayer.position());
            if (serverPlayer.level.addFreshEntity(debugWizard)) {
                return 1;
            }
        }

        throw ERROR_FAILED.create();
    }
}