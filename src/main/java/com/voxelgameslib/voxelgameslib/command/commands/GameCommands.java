package com.voxelgameslib.voxelgameslib.command.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.voxelgameslib.voxelgameslib.config.GlobalConfig;
import com.voxelgameslib.voxelgameslib.game.Game;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.game.GameMode;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.lang.LangKey;
import com.voxelgameslib.voxelgameslib.user.User;
import lombok.extern.java.Log;
import net.kyori.text.TextComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
@Log
@SuppressWarnings("JavaDoc") // commands don't need javadoc, go read the command's descriptions
@CommandAlias("game")
public class GameCommands extends BaseCommand {

    @Inject
    private GameHandler gameHandler;
    @Inject
    private GlobalConfig config;

    @Subcommand("help")
    @CommandAlias("game")
    @CommandPermission("%user")
    public void game(User sender) {
        // todo game help commands, pending a PR to ACF
    }

    @Subcommand("list")
    @CommandPermission("%user")
    public void gameList(User sender) {
        Lang.msg(sender, LangKey.GAME_GAMELIST_HEADER);
        for (Game game : gameHandler.getGames()) {
            Lang.msg(sender, LangKey.GAME_GAMELIST_ENTRY,
                    game.getUuid().toString().split("-")[0], game.getGameMode().getName(),
                    game.getActivePhase().getName(), game.getPlayers().size(), game.getSpectators().size());
        }
        Lang.msg(sender, LangKey.GAME_GAMELIST_FOOTER);
    }

    @Subcommand("modes")
    @CommandPermission("%user")
    public void gameListModes(User sender) {
        StringBuilder sb = new StringBuilder();
        gameHandler.getGameModes().forEach(m -> sb.append(m.getName()).append(", "));
        sb.replace(sb.length() - 2, sb.length(), ".");
        Lang.msg(sender, LangKey.GAME_GAMEMODE_INSTALLED, sb.toString());
    }

    @Subcommand("start")
    @CommandCompletion("@gamemodes")
    @Syntax("<mode> - the mode you want to start")
    @CommandPermission("%premium")
    public void gameStart(User sender, GameMode mode) {
        if (gameHandler.getGames(sender.getUuid(), true).size() != 0) {
            Lang.msg(sender, LangKey.GAME_YOU_CANNOT_BE_IN_MULTIPLE_GAMES);
            return;
        }

        Game game = gameHandler.startGame(mode);

        if (game.getActivePhase().isRunning()) {
            game.join(sender);
            Lang.msg(sender, LangKey.GAME_GAME_STARTED);
            if (config.announceNewGame) {
                //TODO figure out which command to enter
                Lang.broadcast(LangKey.GAME_ANNOUNCE_GAME_STARTED, "game join " + mode.getName(), sender.getDisplayName(), mode.getName());
            }
        } else {
            Lang.msg(sender, LangKey.GAME_COULD_NOT_START);
        }
    }

    @Subcommand("stop")
    @CommandPermission("%admin")
    public void gameStop(User sender, @co.aikar.commands.annotation.Optional String gameId) {
        List<Game> games = gameHandler.getGames(sender.getUuid(), false);
        if (games.size() == 0) {
            Lang.msg(sender, LangKey.GAME_STOP_IN_NO_GAME);
            games = gameHandler.getGames(sender.getUuid(), true);

            if (games.size() == 0) {
                Lang.msg(sender, LangKey.GAME_STOP_IN_NO_GAME_SPEC);
            }
        } else if (games.size() == 1) {
            games.get(0).abortGame();
        }

        if (gameId == null) {
            games.forEach(Game::abortGame);
        } else {
            // todo we need a better way to have game identifiers
            games.get(Integer.parseInt(gameId)).abortGame();
        }

    }

    @Subcommand("join")
    @CommandCompletion("@gamemodes")
    @Syntax("<mode> - the mode you want to start")
    @CommandPermission("%user")
    public void gameJoin(User sender, GameMode mode) {
        Optional<Game> game = gameHandler.findGame(sender, mode);
        if (game.isPresent()) {
            game.get().join(sender);
        } else {
            Lang.msg(sender, LangKey.GAME_NO_GAME_TO_JOIN_FOUND);
        }
    }

    @Subcommand("leave")
    @CommandPermission("%user")
    public void gameLeave(User sender) {
        List<Game> games = gameHandler.getGames(sender.getUuid(), true);

        if (games.size() == 0) {
            Lang.msg(sender, LangKey.GAME_NOT_FOUND);
            return;
        } else if (games.size() > 1) {
            // todo perhaps remove in production
            Lang.msg(sender, LangKey.GAME_IN_MORE_THAN_ONE_GAME);
        }

        games.forEach(game -> game.leave(sender));
    }

    @Subcommand("skip-phase")
    @CommandPermission("%admin")
    public void skipPhase(User sender, @co.aikar.commands.annotation.Optional Integer id) {
        List<Game> games = gameHandler.getGames(sender.getUuid(), true);
        if (id == null) {
            if (games.size() > 1) {
                Lang.msg(sender, LangKey.GAME_IN_TOO_MANY_GAMES);
            } else {
                log.finer("skip " + games.get(0).getActivePhase().getName());
                games.get(0).endPhase();
            }
        } else {
            if (games.size() > id || id < 0) {
                Lang.msg(sender, LangKey.GAME_INVALID_GAME_ID);
            } else {
                log.finer("skip " + games.get(id).getActivePhase().getName());
                games.get(id).endPhase();
            }
        }
    }

    @Subcommand("shout")
    @CommandAlias("shout|s")
    public void shout(User sender, String message) {
        List<Game> games = gameHandler.getGames(sender.getUuid(), true);

        if (games.size() == 0) {
            Lang.msg(sender, LangKey.GAME_NOT_IN_GAME_NO_ID);
            return;
        } else if (games.size() > 1) {
            Lang.msg(sender, LangKey.GAME_IN_TOO_MANY_GAMES);
            return;
        } else {
            games.get(0).broadcastMessage(TextComponent.of(message));
        }
    }
}
