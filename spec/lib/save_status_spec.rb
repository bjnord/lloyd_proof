# Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

require 'spec_helper'

describe SaveStatus do
  # FIXME shouldn't use specific class Correction; should spec
  # generically as in <http://www.ruby-forum.com/topic/214968>
  let(:correction) { Correction.new }

  describe '#save_and_return_status' do
    subject { correction.save_and_return_status(333) }

    context 'with a valid object' do
      before(:each) { correction.should_receive(:save).and_return(true) }
      its([:sync_id]) { should == 333 }
      its([:status]) { should == :ok }
    end

    context 'with an invalid object' do
      before(:each) { correction.should_receive(:save).and_return(false) }
      its([:sync_id]) { should == 333 }
      its([:status]) { should == :error }
    end
  end

end
