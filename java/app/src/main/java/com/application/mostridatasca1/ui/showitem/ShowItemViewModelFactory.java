package com.application.mostridatasca1.ui.showitem;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
public class ShowItemViewModelFactory implements ViewModelProvider.Factory{
    private ObjectRepository objectRepository;

    public ShowItemViewModelFactory(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShowItemViewModel.class)) {
            return (T) new ShowItemViewModel(objectRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
// classe che istanzia il ViewModel con il database come parametro, fatta da Copilot
