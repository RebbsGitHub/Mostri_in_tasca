package com.application.mostridatasca1.ui.playersprofilefragment;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.application.mostridatasca1.database.playerdb.UserRepository;

public class PlayersProfileModelFactory implements ViewModelProvider.Factory {
    private UserRepository userRepository;

    public PlayersProfileModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PlayersProfileViewModel.class)) {
            return (T) new PlayersProfileViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
