def doing_something(num)
  if num == 1
    return '1'
  elsif num > 1
    write_stage(doing_something(num - 1))
  else
    puts 'Incorrect num'
  end
end

def write_stage(previous_string)
  i = 0
  new_string = ''
  if previous_string.size == 1
    new_string = "1#{previous_string}"
  else
    while i < previous_string.size - 1
      k = 1
      while (i <= (previous_string.size - 2)) &&
          (previous_string[i] == previous_string[i + 1])
        k += 1
        i += 1
      end
      new_string += "#{k}#{previous_string[i]}"
      i += 1
      if (i == previous_string.size - 1)
        new_string += "1#{previous_string[i]}"
      end
    end
  end
  return new_string
end

puts doing_something(1)
puts doing_something(2)
puts doing_something(3)
puts doing_something(4)
puts doing_something(5)
puts doing_something(6)
puts doing_something(7)
puts doing_something(8)
puts doing_something(9)
puts doing_something(10)
