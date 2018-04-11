```java
public class CompareTest
{
    @Test
    public void testGetUserById()
    {
        final User u = new User();
        u.setId(1);
        u.setName("TestName");

        // Jmock方式
        Mockery context = new JUnit4Mockery();;
        final UserDao userDao = context.mock(UserDao.class);
        context.checking(new Expectations()
        {
            {
                one(userDao).getUserById(123);
                will(returnValue(u));
            }
        });
        assertEquals("TestName", userDao.getUserById(123).getName());
    }

    @Test
    public void testGetUserById1()
    {
        final User u = new User();
        u.setName("TestName");

        // Jmockit方式
        final UserDao userDao = new UserDaoImpl();
        new mockit.Expectations(userDao)
        {
            {
                userDao.getUserById(123);
                result = u;
            }
        };
        assertEquals("TestName", userDao.getUserById(123).getName());
    }

    @Test
    public void testGetUserById2()
    {
        final User u = new User();
        u.setName("TestName");

        // Mockito方式，非常简洁
        UserDao userDao = mock(UserDao.class);
        when(userDao.getUserById(1)).thenReturn(u);
        assertEquals("TestName", userDao.getUserById(1).getName());
    }

}
```
