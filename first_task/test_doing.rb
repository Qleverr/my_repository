require 'minitest/autorun'
require './doing.rb'
class TestDoing < MiniTest::Test
  def test_1
    assert_equal('1', doing_something(1))
  end

  def test_2
    assert_equal('11', doing_something(2))
  end

  def test_3
    assert_equal('21', doing_something(3))
  end

  def test_4
    assert_equal('1211', doing_something(4))
  end

  def test_5
    assert_equal('111221', doing_something(5))
  end

  def test_6
    assert_equal('312211', doing_something(6))
  end

  def test_7
    assert_equal('13112221', doing_something(7))
  end

  def test_8
    assert_equal('1113213211', doing_something(8))
  end

  def test_9
    assert_equal('31131211131221', doing_something(9))
  end

  def test_10
    assert_equal('13211311123113112211', doing_something(10))
  end
end
