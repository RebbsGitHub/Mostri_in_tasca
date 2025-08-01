import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import ProfileView from './ProfileView';
import RankedView from './RankedView';
import PlayerView from './PlayerView';
import VirtualObjView from './VirtualObjView';

const Stack = createStackNavigator();

export default function ProfileStackScreen() {
    return (
        <Stack.Navigator initialRouteName="ProfilePage" >
            <Stack.Screen name="ProfilePage" component={ProfileView} options={{ headerShown: false }} />
            <Stack.Screen name="RankedPlayers" component={RankedView} options={{ headerShown: false }} />
            <Stack.Screen name="SeePlayer" component={PlayerView} options={{ headerShown: false }} />
            <Stack.Screen name="SeeVirtualObj" component={VirtualObjView} options={{ headerShown: false }} />
        </Stack.Navigator>
    );
}