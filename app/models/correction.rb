class Correction < ActiveRecord::Base
  attr_accessible :current
  validates_presence_of :current
end
