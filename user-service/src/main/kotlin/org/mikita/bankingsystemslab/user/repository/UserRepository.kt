package org.mikita.bankingsystemslab.user.repository

import org.mikita.bankingsystemslab.user.domain.User
import org.mikita.bankingsystemslab.user.domain.UserStatus
import org.mikita.bankingsystemslab.user.exception.UsernameClashException
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.util.Optional
import java.util.regex.Matcher
import java.util.regex.Pattern

@Repository
class UserRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    val pgKeyPattern: Pattern = Pattern.compile("Key \\(([^)]+)\\)=")

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

        try {
            return jdbcTemplate.queryForObject(
                insertQuery,
                userRowMapper,
                user.username,
                user.status.name
            )
        } catch (ex: DuplicateKeyException) {
            val rootMessage: String = if (ex.rootCause != null) ex.rootCause!!.message!! else ex.message!!
            val matcher: Matcher = pgKeyPattern.matcher(rootMessage)

            if (matcher.find()) {
                val duplicatedField = matcher.group(1)

                if ("username" == duplicatedField) {
                    throw UsernameClashException(user.username)
                }
            }
            throw ex
        }
    }
}
