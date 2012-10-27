# Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

class Correction < ActiveRecord::Base
  include ReturnStatus

  attr_accessible :current_text
  validates_presence_of :current_text
end
