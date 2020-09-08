import React from 'react';
import navigation from "../_nav";

export const  UserViews = (currentUser) => {
    return {items : navigation.items.filter(nav => currentUser.username===nav.username) }
}

