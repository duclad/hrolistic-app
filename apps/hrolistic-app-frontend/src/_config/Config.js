let prod = {
    url: {
        API_URL: 'https://myapp.herokuapp.com',
        API_URL_USERS: 'https://myapp.herokuapp.com/users'
    }
};
let dev = {
    url: {
        API_URL: 'http://localhost:8080',
        API_URL_AUTH: 'http://localhost:8080'
    }
};

export const config = process.env.NODE_ENV === 'development' ? dev : prod;
