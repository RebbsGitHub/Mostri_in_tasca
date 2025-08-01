import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import MapView  from './MapView';
import ActivateItemView from '../NearObj/ActivateItemView';
import PlayerView  from '../Profile/PlayerView';

const StackMap = createStackNavigator();

export default function MapStackScreen() {
    return (
        <StackMap.Navigator initialRouteName="MapPage" >
            <StackMap.Screen name="MapPage" component={MapView} options={{ headerShown: false }} />
            <StackMap.Screen name="ActivateItem" component={ActivateItemView} options={{ headerShown: false }} />
            <StackMap.Screen name="SeePlayerOnMap" component={PlayerView} options={{ headerShown: false }} />
        </StackMap.Navigator>
    );
}