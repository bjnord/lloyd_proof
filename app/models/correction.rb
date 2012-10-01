class Correction < ActiveRecord::Base
  attr_accessible :current_text
  validates_presence_of :current_text
end
