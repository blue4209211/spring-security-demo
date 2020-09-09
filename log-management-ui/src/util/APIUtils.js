import { API_BASE_URL, ACCESS_TOKEN } from '../constants';

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    })

    if(localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
    .then(response =>
        response.json().then(json => {
            if(!response.ok) {
                return Promise.reject(json);
            }
            return json;
        })
    );
};

let account = {
    id: "",
}

let accountListeners = []

export function setCurrentAccount(newAccount) {
    account = newAccount;
    accountListeners.forEach(al => {
        al(account);
    })
}
//TODO may result in memoryLeak
export function onCurrentAccountChange(callback) {
    accountListeners.push(callback);
}

export function getCurrentAccount() {
    return account;
}


export function getCurrentUser() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/users/me",
        method: 'GET'
    });
}

export function getCurrentUserEvents() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/users/me/events",
        method: 'GET'
    });
}

export function login(loginRequest) {
    return request({
        url: API_BASE_URL + "/auth/login",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signupRequest) {
    return request({
        url: API_BASE_URL + "/accounts/auth/signup",
        method: 'POST',
        body: JSON.stringify(signupRequest)
    });
}

export function createAccessTokens(agentSignupRequest) {
    return request({
        url: `${API_BASE_URL}/accounts/${account.id}/tokens`,
        method: 'POST',
        body: JSON.stringify(agentSignupRequest)
    });
}
export function listAccessTokens() {
    return request({
        url: `${API_BASE_URL}/accounts/${account.id}/tokens`,
        method: 'GET'
    });
}
export function deleteAccessTokens(agentId) {
    return request({
        url: `${API_BASE_URL}/accounts/${account.id}/tokens/${agentId}`,
        method: 'DELETE'
    });
}

export function deleteUser(userId) {
    return request({
        url: `${API_BASE_URL}/accounts/${account.id}/users/${userId}`,
        method: 'DELETE'
    });
}




export function getCurrentAccountEvents() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: `${API_BASE_URL}/accounts/${account.id}/events`,
        method: 'GET'
    });
}

export function createAccount(accountSignupRequest) {
    return request({
        url: `${API_BASE_URL}/admin/accounts`,
        method: 'POST',
        body: JSON.stringify(accountSignupRequest)
    });
}

export function deleteAccount(accountId) {
    return request({
        url: `${API_BASE_URL}/admin/accounts/${accountId}`,
        method: 'DELETE'
    });
}

export function listAccounts() {
    return request({
        url: `${API_BASE_URL}/admin/accounts`,
        method: 'GET'
    });
}


export function createUser(userRegisterRequest) {
    return request({
        url: `${API_BASE_URL}/accounts/${account.id}/users`,
        method: 'POST',
        body: JSON.stringify(userRegisterRequest)
    });
}
export function listUsers() {
    return request({
        url: `${API_BASE_URL}/accounts/${account.id}/users`,
        method: 'GET'
    });
}
