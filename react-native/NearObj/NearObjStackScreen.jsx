import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';
import NearObjView from './NearObjView';
import ActivateItemView from './ActivateItemView';

const StackOBJ = createStackNavigator();

export default function NearObjStackScreen() {
    return (
        <StackOBJ.Navigator initialRouteName="NearObjPage" >
            <StackOBJ.Screen name="NearObjPage" component={NearObjView} options={{ headerShown: false }} />
            <StackOBJ.Screen name="ActivateItem" component={ActivateItemView} options={{ headerShown: false }} />
        </StackOBJ.Navigator>
    );
}