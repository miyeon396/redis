package com.demo.redis.Service;

//@RequiredArgsConstructor
//@Service
//public class RedisService implements IRedisService {
//    private final RedisUtil<String> redisStringUtil;
//    private final RedisUtil<Programmer> redisProgrammerUtil;
//
//    //region String
//    @Override
//    public void setObjectAsString(String key, String value) {
//        redisStringUtil.putValue(key, value);
//        redisStringUtil.setExpire(key, 1, TimeUnit.HOURS);
//    }
//
//    @Override
//    public String getObjectAsString(String key) {
//        return redisStringUtil.getValue(key);
//    }
//    //endregion
//
//
//    //region List
//    @Override
//    public void addToList(String key, Programmer programmer) {
//        redisProgrammerUtil.addList(key, programmer);
//    }
//
//    @Override
//    public List<Programmer> getListMembers(String key) {
//        return redisProgrammerUtil.getListMembers(key);
//    }
//
//    @Override
//    public Long getListSize(String key) {
//        return redisProgrammerUtil.getListSize(key);
//    }
//    //endregion
//
//
//    //region Set
//    @Override
//    public void addToSet(String key, Programmer... programmers) {
//        redisProgrammerUtil.addToSet(key, programmers);
//    }
//
//    @Override
//    public Set<Programmer> getSetMembers(String key) {
//        return redisProgrammerUtil.getSetMembers(key);
//    }
//    //endregion
//
//
//    //region Hash
//    @Override
//    public void saveHash(String key, Programmer programmer) {
//        redisProgrammerUtil.saveHash(key, programmer.getId(), programmer);
//    }
//
//    @Override
//    public Programmer findInHash(String key, int id) {
//        return redisProgrammerUtil.findInHash(key, id);
//    }
//
//    @Override
//    public void deleteHash(String key, int id) {
//        redisProgrammerUtil.deleteHash(key, id);
//    }
////endregion
//}
