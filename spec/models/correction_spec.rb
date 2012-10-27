# Author::     Brent J. Nordquist <mailto:brent@nordist.net>
# Copyright::  Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# License::    {CC Attribution-ShareAlike 3.0 Unported}[http://creativecommons.org/licenses/by-sa/3.0/]

require 'spec_helper'

describe Correction do
  it "requires current text" do
    corr = FactoryGirl.build(:correction, :current_text => nil)
    corr.should_not be_valid
    corr.errors.should be_added(:current_text, :blank)
  end
end
