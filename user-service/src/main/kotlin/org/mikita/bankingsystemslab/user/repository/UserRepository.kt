package org.mikita.bankingsystemslab.user.repository

import org.mikita.bankingsystemslab.user.domain.User
import org.mikita.bankingsystemslab.user.domain.UserStatus
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.util.Optional

@Repository
class UserRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    @Transactional
    fun findById(id: Long) : Optional<User> {
        val query = "SELECT user_id, username, status FROM users " +
                "WHERE user_id = ?"

        val userRowMapper: RowMapper<User> = RowMapper { rs: ResultSet, `_`: Int ->
            User(
                userId = rs.getLong("user_id"),
                username = rs.getString("username"),
                status = UserStatus.valueOf(rs.getString("status"))
            )
        }

        return try {
            val user = jdbcTemplate.queryForObject(query, userRowMapper, id)
            Optional.of(user)
        } catch (_: EmptyResultDataAccessException) {
            Optional.empty()
        }
    }

    @Transactional
    fun saveNew(user: User) : User {
        val insertQuery = "INSERT INTO users (user_id, username, status) " +
                "VALUES (nextval('user_id_seq'), ?, ?) " +
                "RETURNING user_id, username, status"

        val userRowMapper: RowMapper<User> = RowMapper { rs: ResultSet, `_`: Int ->
            User(
                userId = rs.getLong("user_id"),
                username = rs.getString("username"),
                status = UserStatus.valueOf(rs.getString("status"))
            )
        }

        val savedUser = jdbcTemplate.queryForObject(
            insertQuery,
            userRowMapper,
            user.username,
            user.status.name
        )

        return savedUser
    }
}
