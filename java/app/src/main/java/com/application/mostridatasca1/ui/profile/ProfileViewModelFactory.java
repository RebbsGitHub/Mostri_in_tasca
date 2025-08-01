package com.application.mostridatasca1.ui.profile;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.application.mostridatasca1.database.playerdb.UserRepository;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {
    private UserRepository userRepository;

    public ProfileViewModelFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
// classe che istanzia il ViewModel con il database come parametro, fatta da Copilot