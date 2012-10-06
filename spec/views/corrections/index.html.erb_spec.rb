# Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

require 'spec_helper'

describe "corrections/index.html.erb" do
  before(:all) do
    @corrections = FactoryGirl.build_list(:correction, 2)
  end

  it "renders all corrections" do
    render
    @corrections.each do |correction|
      rendered.should contain(correction.current_text)
    end
  end
end
