package ro.optimizit.hrolistic.app.security;

import ro.optimizit.hrolistic.backend.data.entity.User;

@FunctionalInterface
public interface CurrentUser {

	User getUser();
}
