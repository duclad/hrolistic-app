import { authenticationConstants } from '../_constants';
import { authService } from '../_services';
import { alertActions } from './';
import { history } from '../_helpers';
import * as JWT  from 'jwt-decode';

export const authenticationActions = {
    login,
    logout,
    register
};

function login(username, password) {
    return dispatch => {
        dispatch(request({ username }));

        authService.login(username, password)
            .then(
                user => { 
                    dispatch(success(user));
                    history.push('/dashboard');
                },
                error => {
                    dispatch(failure(error.toString()));
                    dispatch(alertActions.error(error.toString()));
                }
            );
    };

    function request(user) {console.log(user.access_token); return { type: authenticationConstants.LOGIN_REQUEST, user } }
    function success(user) { return { type: authenticationConstants.LOGIN_SUCCESS, user } }
    function failure(error) { return { type: authenticationConstants.LOGIN_FAILURE, error } }
}

function logout() {
    authService.logout();
    return { type: authenticationConstants.LOGOUT };
}

function register(user) {
    return dispatch => {
        dispatch(request(user));

        authService.register(user)
            .then(
                user => { 
                    dispatch(success());
                    history.push('/login');
                    dispatch(alertActions.success('Registration successful'));
                },
                error => {
                    dispatch(failure(error.toString()));
                    dispatch(alertActions.error(error.toString()));
                }
            );
    };

    function request(user) { return { type: authenticationConstants.REGISTER_REQUEST, user } }
    function success(user) { return { type: authenticationConstants.REGISTER_SUCCESS, user } }
    function failure(error) { return { type: authenticationConstants.REGISTER_FAILURE, error } }
}