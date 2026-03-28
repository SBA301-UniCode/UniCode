package com.example.unicode;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class DbCheckerTest {

    @Test
    void constructorShouldBeCallable() {
        DbChecker checker = new DbChecker();
        org.junit.jupiter.api.Assertions.assertEquals(DbChecker.class, checker.getClass());
    }

    @Test
    void mainShouldHandleConnectionErrorsWithoutThrowing() {
        assertDoesNotThrow(() -> DbChecker.main(new String[0]));
    }

    @Test
    void mainShouldReadRowsWhenConnectionSucceeds() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("document_id")).thenReturn("d1");
        when(resultSet.getString("title")).thenReturn("Doc");
        when(resultSet.getString("document_url")).thenReturn("url");

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(connection);

            assertDoesNotThrow(() -> DbChecker.main(new String[0]));
        }

        verify(connection).createStatement();
        verify(statement).executeQuery(anyString());
        verify(resultSet, atLeastOnce()).next();
    }
}
