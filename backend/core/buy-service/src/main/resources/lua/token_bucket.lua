local currentTokens = tonumber(redis.call('get', KEYS[1]))
if currentTokens == nil then
    currentTokens = tonumber(ARGV[1])
end
local lastRefillTime = tonumber(redis.call('get', KEYS[2]))
if lastRefillTime == nil then
    lastRefillTime = tonumber(ARGV[2])
end
local now = tonumber(ARGV[3])
local elapsed = now - lastRefillTime
local newTokens = math.floor(elapsed * tonumber(ARGV[4]) / 1000)
currentTokens = math.min(tonumber(ARGV[1]), currentTokens + newTokens)
redis.call('set', KEYS[1], currentTokens)
redis.call('set', KEYS[2], now)
if currentTokens >= tonumber(ARGV[5]) then
    redis.call('decrby', KEYS[1], tonumber(ARGV[5]))
    return 1
else
    return 0
end