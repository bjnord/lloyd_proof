# Author::     Brent J. Nordquist <mailto:brent@nordist.net>
# Copyright::  Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# License::    {CC Attribution-ShareAlike 3.0 Unported}[http://creativecommons.org/licenses/by-sa/3.0/]

require 'spec_helper'

# FIXME shouldn't use specific class Correction; should spec
# generically as in <http://www.ruby-forum.com/topic/214968>

describe ReturnStatus do

  describe '#create_and_return_status' do
    let(:correction_attr) { Hash[:sync_id => sync_id] }
    let(:sync_id) { 444 }
    subject { Correction.create_and_return_status(correction_attr) }

    context 'with a valid object' do
      before(:each) { Correction.should_receive(:create).and_return(mock_model(Correction)) }
      its([:sync_id]) { should == sync_id }
      its([:status]) { should == :ok }
    end

    context 'with an invalid object' do
      before(:each) { Correction.should_receive(:create).and_return(mock_model(Correction, :persisted? => false)) }
      its([:sync_id]) { should == sync_id }
      its([:status]) { should == :error }
    end

    context 'with a mass-assignment attempt' do
      before(:each) { Correction.should_receive(:create).and_raise(ActiveModel::MassAssignmentSecurity::Error) }
      its([:sync_id]) { should == sync_id }
      its([:status]) { should == :error }
      its([:errors]) { should_not be_nil }
    end
  end

  describe '#save_and_return_status' do
    let(:correction) { Correction.new }
    let(:sync_id) { 333 }
    subject { correction.save_and_return_status(sync_id) }

    context 'with a valid object' do
      before(:each) { correction.should_receive(:save).and_return(true) }
      its([:sync_id]) { should == sync_id }
      its([:status]) { should == :ok }
    end

    context 'with an invalid object' do
      before(:each) { correction.should_receive(:save).and_return(false) }
      its([:sync_id]) { should == sync_id }
      its([:status]) { should == :error }
    end
  end

end
