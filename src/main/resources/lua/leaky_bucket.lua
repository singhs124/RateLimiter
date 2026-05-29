local bucketKey = KEYS[1]
local queueKey = KEYS[2]

local capacity = tonumber(ARGV[1])
local leakRatePerSec = tonumber(ARGV[2])
local currentTime = tonumber(ARGV[3])
local isConsuming = tonumber(ARGV[4]) == 1

local lastLeakTime = redis.call('HGET',bucketKey,'last_leak_time')

if not lastLeakTime then
	lastLeakTime = currentTime
	redis.call('HSET', bucketKey, 'last_leak_time', lastLeakTime)
else
	lastLeakTime = tonumber(lastLeakTime)
end

local elapsedMs = currentTime - lastLeakTime
local elapsedSec = elapsedMs / 1000
local leaksAllowed = elapsedSec*leakRatePerSec
local leaksAllowedToFloor = math.floor(leaksAllowed)
if leaksAllowedToFloor > 0 then
	for i = 1, leaksAllowedToFloor do
		redis.log(redis.LOG_WARNING, "Leaking requests from queue: " ..leaksAllowedToFloor)
		local ele = redis.call('RPOP', queueKey)
		redis.call('HSET', bucketKey, 'last_leak_time', currentTime)
		if not ele then break end
	end
end

local queueSize = redis.call('LLEN', queueKey)
local numberOfElementsQueueCanAccomodate = capacity - queueSize
local allowed = numberOfElementsQueueCanAccomodate > 0
if isConsuming and allowed then
	redis.call('LPUSH', queueKey, currentTime)
	numberOfElementsQueueCanAccomodate = numberOfElementsQueueCanAccomodate-1
end

redis.call('EXPIRE', bucketKey, 3600)
redis.call('EXPIRE', queueKey, 3600)
redis.call('HSET', bucketKey, 'last_access', currentTime)

return {allowed and 1 or 0,numberOfElementsQueueCanAccomodate}
