-- KEYS[1] = bucket key
-- ARGV[1] = capacity
-- ARGV[2] = refill rate (tokens/sec)
-- ARGV[3] = current timestamp (ms)
-- Returns: [allowed, remaining_tokens, reset_time_ms]

local key = KEYS[1]
local maxBucketSize = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local currentTime = tonumber(ARGV[3])
local isConsuming = tonumber(ARGV[4]) == 1

redis.log(redis.LOG_WARNING, "Starting token bucket script")
redis.log(redis.LOG_WARNING, "key=" .. key ..
", maxBucketSize=" .. maxBucketSize ..
", refillRate=" .. refillRate ..
", currentTime=" .. currentTime)


-- Load state
local tokens = tonumber(redis.call('HGET', key, 'tokens'))
local lastRefill = tonumber(redis.call('HGET', key, 'last_refill'))
redis.log(redis.LOG_WARNING, tokens)
redis.log(redis.LOG_WARNING, lastRefill)

if tokens == nil or lastRefill == nil then
	tokens = maxBucketSize
	lastRefill = currentTime
	redis.call('HSET',key,'tokens',tokens)
	redis.call('HSET',key,'last_refill',lastRefill)
else --Refilling
	local elapsedMs = currentTime - lastRefill
	local elapsedSec = elapsedMs / 1000
	local tokensToAdd = elapsedSec * refillRate
	local tokensToAddFloor = math.floor(tokensToAdd)
	redis.log(redis.LOG_WARNING, "tokensToAdd:" ..tokensToAddFloor)
	if tokensToAddFloor > 0 then
		tokens = math.min(maxBucketSize, tokens + tokensToAddFloor)
		redis.call('HSET', key, 'tokens', tokens)
		redis.call('HSET', key, 'last_refill', currentTime)
	end
end

local allowed = tokens >= 1
if isConsuming and allowed then
	tokens = tokens - 1
	redis.log(redis.LOG_WARNING, "Decreasing Token Count after Cosnuming:" ..tokens)
	redis.call('HSET', key, 'tokens', tokens)
end

redis.call('EXPIRE', key, 3600)
redis.call('HSET', key, 'last_access', currentTime)

-- Return {allowed, remaining_tokens}
return {allowed and 1 or 0, tokens}

--if new_tokens >= 1 then
--	new_tokens = new_tokens-1
--	redis.call('HSET', key, 'tokens', new_tokens)
--	redis.call('HSET', key, 'last_refill', current_time)
--	redis.call('EXPIRE', key, 3600)
--
--	local tokens_needed = capacity - new_tokens
--	local seconds_to_full = tokens_needed / refill_rate
--	local reset_time = current_time + (seconds_to_full*1000)
--
--	return {1, new_tokens, reset_time}
--else
--	local tokens_nedded = 1 - new_tokens
--	local retry_after = math.ceil(tokens_nedded/refill_rate)
--
--	return {0,0,current_time+(retry_after*1000),retry_after}
--end