/*
 *  Copyright (C) 2022 Lucas B. R. de Oliveira - IFSP/SCL
 *  Contact: lucas <dot> oliveira <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */

package com.bueno.domain.usecases.hand;

import com.bueno.domain.entities.game.Game;
import com.bueno.domain.entities.hand.Hand;
import com.bueno.domain.entities.intel.PossibleAction;
import com.bueno.domain.entities.player.Player;
import com.bueno.domain.usecases.bot.BotUseCase;
import com.bueno.domain.usecases.game.GameRepository;
import com.bueno.domain.usecases.utils.dtos.IntelDto;
import com.bueno.domain.usecases.utils.converters.IntelConverter;
import com.bueno.domain.usecases.utils.validation.Notification;
import com.bueno.domain.usecases.utils.exceptions.UnsupportedGameRequestException;
import com.bueno.domain.usecases.utils.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class PointsProposalUseCase {

    private final GameRepository repo;
    private final BotUseCase botUseCase;

    public PointsProposalUseCase(GameRepository repo) {
        this.repo = Objects.requireNonNull(repo);
        this.botUseCase = new BotUseCase(repo);
    }

    public IntelDto raise(UUID usedUuid){
        validateInput(usedUuid, PossibleAction.RAISE);

        final Game game = repo.findByUserUuid(usedUuid).orElseThrow();
        final Hand hand = game.currentHand();
        final Player player = hand.getCurrentPlayer();

        hand.raise(player);
        botUseCase.playWhenNecessary(game);

        return IntelConverter.of(game.getIntel());
    }

    public IntelDto accept(UUID usedUuid){
        validateInput(usedUuid, PossibleAction.ACCEPT);

        final Game game = repo.findByUserUuid(usedUuid).orElseThrow();
        final Hand hand = game.currentHand();
        final Player player = hand.getCurrentPlayer();

        hand.accept(player);
        botUseCase.playWhenNecessary(game);

        return IntelConverter.of(game.getIntel());
    }

    public IntelDto quit(UUID usedUuid){
        validateInput(usedUuid, PossibleAction.QUIT);

        final Game game = repo.findByUserUuid(usedUuid).orElseThrow();
        final Hand hand = game.currentHand();
        final Player player = hand.getCurrentPlayer();

        hand.quit(player);
        hand.getResult().ifPresent(unused -> updateGameStatus(game));

        if(game.isDone()) return IntelConverter.of(game.getIntel());
        botUseCase.playWhenNecessary(game);

        return IntelConverter.of(game.getIntel());
    }

    private void validateInput(UUID usedUuid, PossibleAction raise) {
        final Validator<UUID> validator = new ActionValidator(repo, raise);
        final Notification notification = validator.validate(usedUuid);
        if (notification.hasErrors()) throw new UnsupportedGameRequestException(notification.errorMessage());
    }

    private void updateGameStatus(Game game) {
        game.updateScores();
        if(!game.isDone()) game.prepareNewHand();
    }
}